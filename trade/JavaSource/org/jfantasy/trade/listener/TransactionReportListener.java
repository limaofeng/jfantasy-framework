package org.jfantasy.trade.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.BillType;
import org.jfantasy.trade.bean.enums.ReportTargetType;
import org.jfantasy.trade.bean.enums.TimeUnit;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
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

    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        if (transaction.getStatus() != TxStatus.success) {
            return;
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
