package org.jfantasy.trade.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.security.SpringSecurityUtils;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.oauth.userdetails.OAuthUserDetails;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.rest.models.PayForm;
import org.jfantasy.pay.rest.models.TxStatusForm;
import org.jfantasy.pay.rest.models.assembler.TransactionResourceAssembler;
import org.jfantasy.pay.service.PayConfigService;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.AccountStatus;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.service.AccountService;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 交易记录
 **/
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    public static final TransactionResourceAssembler assembler = new TransactionResourceAssembler();

    private final ProjectService projectService;
    private final TransactionService transactionService;
    private final PayService payService;
    private final PayConfigService configService;
    private final AccountService accountService;

    @Autowired
    public TransactionController(ProjectService projectService, PayConfigService configService, PayService payService, TransactionService transactionService, AccountService accountService) {
        this.projectService = projectService;
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
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> seach(Pager<Transaction> pager, List<PropertyFilter> filters) {
        if (!pager.isOrderBySetted()) {
            pager.setOrderBy(Transaction.FIELDS_BY_CREATE_TIME);
            pager.setOrder(Pager.SORT_DESC);
        }
        return assembler.toResources(transactionService.findPager(pager, filters));
    }

    /**
     * 获取交易详情
     *
     * @param sn 交易流水
     * @return Transaction
     */
    @GetMapping("/{id}")
    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms", "default", "disabled"}))
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String sn) {
        return transform(get(sn));
    }

    @GetMapping("/{id}/payments")
    @ResponseBody
    public List<Payment> payments(@PathVariable("id") String id) {
        return this.get(id).getPayments();
    }

    @GetMapping("/{id}/refunds")
    @ResponseBody
    public List<Refund> refunds(@PathVariable("id") String id) {
        return this.get(id).getRefunds();
    }

    @PutMapping("/{id}/status")
    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms"}))
    @ResponseBody
    public Transaction status(@PathVariable("id") String sn, @Validated @RequestBody TxStatusForm form) {
        return this.transactionService.update(sn, form.getStatus(), form.getStatusText(), form.getNotes());
    }

    /**
     * 获取支付表单进行支付
     **/
    @PostMapping("/{id}/pay-form")
    @ResponseBody
    @JsonResultFilter(allow = @AllowProperty(pojo = Payment.class, name = {"sn", "type", "pay_config_name", "total_amount", "payment_fee", "status", "source"}))
    public Object payForm(@PathVariable("id") String sn, @RequestBody PayForm payForm) throws PayException {
        Transaction transaction = get(sn);
        if (transaction == null) {
            throw new ValidationException(100000, "[" + sn + "]交易不存在");
        }
        if (Project.PAYMENT.equals(transaction.getProject())) {
            return payService.pay(transaction, payForm.getPayconfigId(), payForm.getPayType(), payForm.getPayer(), payForm.getProperties());
        } else if (Project.REFUND.equals(transaction.getProject())) {
            return payService.refund(transaction);
        }
        throw new PayException(String.format("不支持该项目[%s]", transaction.getProject()));
    }

    private Transaction get(String id) {
        return transactionService.get(id);
    }

    public ResultResourceSupport transform(Transaction transaction) {
        ResultResourceSupport resource = assembler.toResource(transaction);
        OAuthUserDetails user = SpringSecurityUtils.getCurrentUser(OAuthUserDetails.class);

        if (user == null) {
            return resource;
        }

        Project project = this.projectService.get(transaction.getProject());

        if (project.getType() == ProjectType.order) {
            switch (transaction.get(Transaction.STAGE)) {
                case Transaction.STAGE_PAYMENT:
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
        List<PayConfig> payconfigs = ObjectUtil.filter(configService.find(), item -> item.getEnabled() && item.getPlatforms().contains(user.getPlatform()));
        // 判断余额支付，金额是否可以支付
        PayConfig payConfig = ObjectUtil.find(payconfigs, "payProductId", "walletpay");
        if (payConfig != null &&(AccountStatus.unactivated.equals(accountService.get(transaction.getFrom()).getStatus())||accountService.get(transaction.getFrom()).getAmount().compareTo(transaction.getAmount()) < 0)) {
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
