package org.jfantasy.pay.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.ThreadJacksonMixInHolder;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.util.common.BeanFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.oauth.userdetails.OAuthUserDetails;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.ProjectType;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.order.entity.enums.PaymentStatus;
import org.jfantasy.pay.rest.models.PayForm;
import org.jfantasy.pay.rest.models.assembler.TransactionResourceAssembler;
import org.jfantasy.pay.service.AccountService;
import org.jfantasy.pay.service.PayConfigService;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.pay.service.TransactionService;
import org.jfantasy.pay.service.vo.ToPayment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易记录
 **/
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private static final Log LOG = LogFactory.getLog(TransactionController.class);

    public static final TransactionResourceAssembler assembler = new TransactionResourceAssembler();

    private final TransactionService transactionService;
    private final PayService payService;
    private final PayConfigService configService;
    private final AccountService accountService;

    @Autowired
    public TransactionController(PayConfigService configService, PayService payService, TransactionService transactionService, AccountService accountService) {
        this.configService = configService;
        this.payService = payService;
        this.transactionService = transactionService;
        this.accountService = accountService;
    }

    /**
     * 查询交易记录
     **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<ResultResourceSupport> seach(Pager<Transaction> pager, List<PropertyFilter> filters) {
        return assembler.toResources(transactionService.findPager(pager, filters));
    }

    /**
     * 获取交易详情
     *
     * @param sn 交易流水
     * @return Transaction
     */
    @RequestMapping(method = RequestMethod.GET, value = "/{id}")
    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms", "default", "disabled"}))
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String sn) {
        return transform(get(sn));
    }

    /**
     * 获取支付表单进行支付
     **/
    @RequestMapping(method = RequestMethod.POST, value = "/{id}/pay-form")
    @ResponseBody
    @JsonResultFilter(allow = @AllowProperty(pojo = Payment.class, name = {"sn", "type", "pay_config_name", "total_amount", "payment_fee", "status", "source"}))
    public ToPayment payForm(@PathVariable("id") String sn, @RequestBody PayForm payForm) throws PayException {
        Transaction transaction = get(sn);
        if (transaction == null) {
            throw new ValidationException(404, "[" + sn + "]交易不存在");
        }
        return payService.pay(transaction, payForm.getPayconfigId(), payForm.getPayType(), payForm.getPayer(), payForm.getProperties());
    }

    private Transaction get(String id) {
        return transactionService.get(id);
    }

    ResultResourceSupport transform(Transaction transaction) {
        ResultResourceSupport resource = assembler.toResource(transaction);
        OAuthUserDetails user = SpringSecurityUtils.getCurrentUser(OAuthUserDetails.class);

        if (user == null) {
            return resource;
        }

        if (transaction.getProject().getType() == ProjectType.order) {
            switch (transaction.get(Transaction.STAGE)) {
                case Transaction.STAGE_PAYMENT:
                    if (!ThreadJacksonMixInHolder.getMixInHolder().isReturnProperty(PayConfig.class, "payconfigs")) {
                        break;
                    }
                    resource.set("payconfigs", payconfigs(transaction, user));
                    break;
                case Transaction.STAGE_REFUND:
                    break;
                default:
            }
        }
        return resource;
    }

    private List<PayConfig> payconfigs(Transaction transaction, final OAuthUserDetails user) {
        // 按平台过滤掉不能使用的支付方式
        List<PayConfig> payconfigs = ObjectUtil.filter(configService.find(), new BeanFilter<PayConfig>() {
            @Override
            public boolean accept(PayConfig item) {
                return item.getPlatforms().contains(user.getPlatform());
            }
        });
        // 判断余额支付，金额是否可以支付
        PayConfig payConfig = ObjectUtil.find(payconfigs, "payProductId", "walletpay");
        if (payConfig != null && accountService.get(transaction.getFrom()).getAmount().compareTo(transaction.getAmount()) < 0) {
            payconfigs.remove(payConfig);
            payConfig.setDisabled(true);
            payconfigs.add(payConfig);
        }
        // 设置默认支付方式
        String paymentsn = transaction.get("payment_sn");
        if (StringUtil.isNotBlank(paymentsn)) {
            Payment payment = ObjectUtil.find(transaction.getPayments(), "sn", paymentsn);
            if (payment.getStatus() == PaymentStatus.ready) { // 加载支付方式
                payConfig = ObjectUtil.find(payconfigs, "payProductId", payment.getPayConfig().getPayProductId());
                if (payConfig.getDisabled() != Boolean.TRUE) {
                    payConfig.setDefault(true);
                }
            }
        } else if (!payconfigs.isEmpty()) {
            payconfigs.get(0).setDefault(true);
        }
        return payconfigs;
    }

}
