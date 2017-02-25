package org.jfantasy.trade.listener;

import org.jfantasy.trade.event.TransactionFlowEvent;
import org.jfantasy.trade.service.ReportService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ReportByTransactionListener implements ApplicationListener<TransactionFlowEvent> {

    private final ReportService reportService;
    private final TransactionService transactionService;

    @Autowired
    public ReportByTransactionListener(ReportService reportService, TransactionService transactionService) {
        this.reportService = reportService;
        this.transactionService = transactionService;
    }

    @Override
    public void onApplicationEvent(TransactionFlowEvent event) {
        // TODO 通过 event.getFlow() 实现更加精准的统计逻辑
        this.reportService.analyze(transactionService.get(event.getTxId()));
    }

}
