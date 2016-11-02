package org.jfantasy.pay.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.event.TransactionChangedEvent;
import org.jfantasy.pay.order.entity.OrderKey;
import org.jfantasy.pay.order.entity.enums.PaymentStatus;
import org.jfantasy.pay.service.PaymentService;
import org.jfantasy.pay.service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 退款处理
 */
@Component
public class TransactionOrderRefundListener implements ApplicationListener<TransactionChangedEvent> {

    private static final Log LOG = LogFactory.getLog(TransactionOrderRefundListener.class);

    private RefundService refundService;
    private PaymentService paymentService;

    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        if (event.getStatus() == TxStatus.unprocessed && Project.REFUND.equals(transaction.getProject())) {
            OrderKey key = OrderKey.newInstance(transaction.get(Transaction.ORDER_KEY));
            List<Payment> payments = paymentService.find(Restrictions.eq("order.sn", key.getSn()), Restrictions.eq("order.type", key.getType()));
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
