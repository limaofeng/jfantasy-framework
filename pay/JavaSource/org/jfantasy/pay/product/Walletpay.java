package org.jfantasy.pay.product;

import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.bean.Order;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.order.entity.enums.PaymentStatus;
import org.jfantasy.pay.order.entity.enums.RefundStatus;
import org.jfantasy.pay.service.AccountService;
import org.jfantasy.pay.service.PayService;

import java.util.Properties;

/**
 * 钱包支付
 */
public class Walletpay extends PayProductSupport {

    private static final String PROPERTY_PW_NAME = "password";

    private PayService payService;
    private AccountService accountService;

    private AccountService accountService() {
        if (accountService == null) {
            accountService = SpringContextUtil.getBeanByType(AccountService.class);
        }
        return accountService;
    }

    private PayService payService() {
        if (payService == null) {
            payService = SpringContextUtil.getBeanByType(PayService.class);
        }
        return payService;
    }

    @Override
    public Object web(Payment payment, Order order, Properties properties) throws PayException {
        return this.transaction(payment, properties);
    }

    public Object transaction(Payment payment, Properties properties) throws PayException {
        //获取支付账户 与 支付密码
        String password = properties.getProperty(PROPERTY_PW_NAME);
        Transaction transaction = (Transaction) properties.get(PROPERTY_TRANSACTION);
        //进行划账操作
        this.accountService().transfer(transaction.getSn(), password, transaction.getNotes());
        //触发通知
        return this.payService().paymentNotify(payment.getSn(), "");
    }

    @Override
    public Object app(Payment payment, Order order, Properties properties) throws PayException {
        return this.transaction(payment, properties);
    }

    @Override
    public Object payNotify(Payment payment, String result) throws PayException {
        payment.setTradeNo(payment.getTransaction().getSn());
        payment.setTradeTime(DateUtil.now());
        payment.setStatus(PaymentStatus.success);
        return "success";
    }

    @Override
    public Object refund(Refund refund) throws PayException {
        refund.setStatus(RefundStatus.success);
        this.accountService().refund(refund.getPayment().getTransaction().getSn(), refund.getTotalAmount(), "退款");
        return this.payService().refundNotify(refund.getSn(), "");
    }

    @Override
    public Object payNotify(Refund refund, String result) throws PayException {
        return "success";
    }

    @Override
    public PaymentStatus query(Payment payment) throws PayException {
        return null;
    }

    @Override
    public void close(Payment payment) throws PayException {
        // Do nothing
    }

}
