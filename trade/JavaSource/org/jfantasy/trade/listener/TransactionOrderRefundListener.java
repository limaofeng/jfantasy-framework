package org.jfantasy.trade.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.service.PaymentService;
import org.jfantasy.pay.service.RefundService;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.TransactionAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 退款处理
 */
@Component
public class TransactionOrderRefundListener implements ApplicationListener<TransactionAddedEvent> {

    private static final Log LOG = LogFactory.getLog(TransactionOrderRefundListener.class);

    private RefundService refundService;
    private PaymentService paymentService;

    @Override
    public void onApplicationEvent(TransactionAddedEvent event) {
        Transaction transaction = event.getTransaction();
        if (event.getStatus() == TxStatus.unprocessed && Project.REFUND.equals(transaction.getProject())) {
            String id = transaction.get(Transaction.ORDER_ID);
            List<Payment> payments = paymentService.find(Restrictions.eq("order.id", id));
            Payment payment = ObjectUtil.find(payments, "status", PaymentStatus.success);
            if (payment == null) {
                LOG.error(" 订单可能未支付成功或者已经退款! ");
                return;
            }
            refundService.create(payment, transaction.getAmount(), transaction, "退款");
        }
    }

    @Autowired
    public void setRefundService(RefundService refundService) {
        this.refundService = refundService;
    }

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

}
