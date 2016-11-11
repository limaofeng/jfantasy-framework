package org.jfantasy.pay.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.BillType;
import org.jfantasy.pay.bean.enums.ReportTargetType;
import org.jfantasy.pay.bean.enums.TimeUnit;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.event.TransactionChangedEvent;
import org.jfantasy.pay.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class TransactionReportListener implements ApplicationListener<TransactionChangedEvent> {

    private static final Log LOGGER = LogFactory.getLog(TransactionReportListener.class);

    private final ReportService reportService;

    @Autowired
    public TransactionReportListener(ReportService reportService) {
        this.reportService = reportService;
    }

    @Async
    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        if (transaction.getStatus() != TxStatus.success) {
            return;
        }
        // 延时5秒后执行，统计
        try {
            Thread.sleep(java.util.concurrent.TimeUnit.SECONDS.toMillis(5));
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted!", e);
            Thread.currentThread().interrupt();
        }

        String day = DateUtil.format(transaction.getModifyTime(), "yyyyMMdd");
        BigDecimal amount = transaction.getAmount();
        String code = transaction.getProject() + (StringUtil.isBlank(transaction.getSubject()) ? "" : ("-" + transaction.getSubject()));
        if (Project.PAYMENT.equals(transaction.getProject()) || Project.REFUND.equals(transaction.getProject()) || Project.INCOME.equals(transaction.getProject())) {
            //记录出帐
            reportService.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.credit, code, amount);
            //记录入帐
            reportService.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.debit, code, amount);
        } else if (Project.INPOUR.equals(transaction.getProject())) {
            //记录入帐
            reportService.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.debit, code, amount);
        } else if (Project.WITHDRAWAL.equals(transaction.getProject())) {
            //记录出帐
            reportService.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.credit, code, amount);
        }
    }


}
