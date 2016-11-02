package org.jfantasy.pay.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.BillType;
import org.jfantasy.pay.bean.enums.ReportTargetType;
import org.jfantasy.pay.bean.enums.TimeUnit;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@ActiveProfiles("dev")
public class ReportServiceTest {

    @Autowired
    private ReportService reportService;
    @Autowired
    private TransactionService transactionService;

    @Test
    public void analyze() throws Exception {
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("EQE_status", TxStatus.success));
        Pager<Transaction> pager = new Pager<>();
        do {
            pager = transactionService.findPager(pager, filters);
            for (Transaction transaction : pager.getPageItems()) {
                String day = DateUtil.format(transaction.getModifyTime(), "yyyyMMdd");
                BigDecimal amount = transaction.getAmount();
                String code = transaction.getProject().getKey() + (StringUtil.isBlank(transaction.getSubject()) ? "" : ("-" + transaction.getSubject()));

                if (Project.PAYMENT.equals(transaction.getProject().getKey()) || Project.REFUND.equals(transaction.getProject().getKey()) || Project.INCOME.equals(transaction.getProject().getKey())) {
                    //记录出帐
                    reportService.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.credit, code, amount);
                    //记录入帐
                    reportService.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.debit, code, amount);
                } else if (Project.INPOUR.equals(transaction.getProject().getKey())) {
                    //记录入帐
                    reportService.analyze(ReportTargetType.account, transaction.getTo(), TimeUnit.day, day, BillType.debit, code, amount);
                } else if (Project.WITHDRAWAL.equals(transaction.getProject().getKey())) {
                    //记录出帐
                    reportService.analyze(ReportTargetType.account, transaction.getFrom(), TimeUnit.day, day, BillType.credit, code, amount);
                }
            }
            pager.setCurrentPage(pager.getCurrentPage() + 1);
            pager.setFirst(0);
        } while (pager.getCurrentPage() <= pager.getTotalPage());

    }

}