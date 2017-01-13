package org.jfantasy.trade.listener;

import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.*;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionReportListener implements ApplicationListener<TransactionChangedEvent> {

    private final ReportService reportService;
    private final ProjectService projectService;

    @Autowired
    public TransactionReportListener(ReportService reportService, ProjectService projectService) {
        this.reportService = reportService;
        this.projectService = projectService;
    }

    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        if (transaction.getStatus() == TxStatus.processing || transaction.getStatus() == TxStatus.close) {
            return;
        }
        Project project = this.projectService.get(transaction.getProject());

        if (transaction.getStatus() == TxStatus.success) {
            String day = DateUtil.format(transaction.getModifyTime(), "yyyyMMdd");
            BigDecimal amount = transaction.getAmount();
            String code = transaction.getProject() + (StringUtil.isBlank(transaction.getSubject()) ? "" : ("-" + transaction.getSubject()));

            if (project.getType() == ProjectType.order || project.getType() == ProjectType.transfer) {
                reportService.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.credit, code, amount);//记录出帐
                reportService.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.debit, code, amount);//记录入帐
            } else if (project.getType() == ProjectType.deposit) {
                reportService.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.debit, code, amount);//记录入帐
            }
        } else if (transaction.getStatus() == TxStatus.unprocessed && ProjectType.withdraw == project.getType()) {
            String day = DateUtil.format(transaction.getModifyTime(), "yyyyMMdd");
            BigDecimal amount = transaction.getAmount();
            String code = transaction.getProject() + (StringUtil.isBlank(transaction.getSubject()) ? "" : ("-" + transaction.getSubject()));

            reportService.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.credit, code, amount);//记录出帐
        }
    }

}
