package org.jfantasy.pay.product;

import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.bean.enums.RefundStatus;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.service.TransactionService;

import java.util.Properties;

/**
 * 钱包支付
 */
public class Walletpay extends PayProductSupport {

    private static final String PROPERTY_PW_NAME = "password";

    private PayService payService;
    private TransactionService transactionService;

    private TransactionService transactionService() {
        if (transactionService == null) {
            transactionService = SpringContextUtil.getBeanByType(TransactionService.class);
        }
        return transactionService;
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
        this.transactionService().handle(transaction.getSn(), password, transaction.getNotes());
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
        //获取支付账户 与 支付密码
        Transaction transaction = refund.getTransaction();
        //进行划账操作
        this.transactionService().handle(transaction.getSn(), transaction.getNotes());
        //触发通知
        return this.payService().refundNotify(refund.getSn(), "");
    }

    @Override
    public Object payNotify(Refund refund, String result) throws PayException {
        refund.setTradeNo(refund.getTransaction().getSn());
        refund.setTradeTime(DateUtil.now());
        refund.setStatus(RefundStatus.success);
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
