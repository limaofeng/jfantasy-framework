package org.jfantasy.trade.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.TransactionAddedEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 退款处理
 */
@Component
public class TransactionOrderRefundListener implements ApplicationListener<TransactionAddedEvent> {

    private static final Log LOG = LogFactory.getLog(TransactionOrderRefundListener.class);

    private PayService payService;

    @Override
    public void onApplicationEvent(TransactionAddedEvent event) {
        Transaction transaction = event.getTransaction();
        if (event.getStatus() == TxStatus.unprocessed && Project.REFUND.equals(transaction.getProject())) {
            payService.refund(transaction);
        }
    }

    @Autowired
    public void setPayService(PayService payService) {
        this.payService = payService;
    }

}
