package org.jfantasy.trade.listener;

import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ReportByTransactionListener implements ApplicationListener<TransactionChangedEvent> {

    private final ReportService reportService;

    @Autowired
    public ReportByTransactionListener(ReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        this.reportService.analyze(transaction);
    }

}
