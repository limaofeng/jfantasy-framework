package org.jfantasy.pay.service;

import org.hibernate.Session;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.lucene.dao.hibernate.OpenSessionUtils;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.pay.bean.Card;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.dao.TransactionDao;
import org.jfantasy.pay.order.entity.OrderKey;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionDao transactionDao;

    @Test
    public void modifyProject() {
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("LIKES_project", "%{"));
        Pager<Transaction> pager = new Pager<>();
        do {
            pager = transactionService.findPager(pager, filters);for (Transaction transaction : pager.getPageItems()) {
                Project project = JSON.deserialize(transaction.getProject(), Project.class);
                Session session = OpenSessionUtils.openSession();
                try {
                    transactionDao.batchSQLExecute("update pay_transaction set project = ? where sn = ?", project.getKey(), transaction.getSn());
                } finally {
                    OpenSessionUtils.closeSession(session);
                }
            }
            pager.setCurrentPage(pager.getCurrentPage() + 1);
            pager.setFirst(0);
        } while (pager.getTotalCount() > 0);
    }

    @Test
    public void modifySubject() {
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("NULL_subject"));
        Pager<Transaction> pager = new Pager<>();
        do {
            pager = transactionService.findPager(pager, filters);
            for (Transaction transaction : pager.getPageItems()) {
                if (Project.PAYMENT.equals(transaction.getProject()) || Project.INCOME.equals(transaction.getProject()) || Project.REFUND.equals(transaction.getProject())) {
                    Session session = OpenSessionUtils.openSession();
                    try {
                        transactionDao.batchSQLExecute("update pay_transaction set subject = ? where sn = ?", OrderKey.newInstance(transaction.get("order_key")).getType(), transaction.getSn());
                    } finally {
                        OpenSessionUtils.closeSession(session);
                    }
                } else if (Project.INPOUR.equals(transaction.getProject())) {
                    Session session = OpenSessionUtils.openSession();
                    try {
                        transactionDao.batchSQLExecute("update pay_transaction set subject = ? where sn = ?", Card.SUBJECT_BY_CARD_INPOUR, transaction.getSn());
                    } finally {
                        OpenSessionUtils.closeSession(session);
                    }
                }
            }
            pager.setCurrentPage(pager.getCurrentPage() + 1);
            pager.setFirst(0);
        } while (pager.getCurrentPage() <= pager.getTotalPage());
    }

    @Test
    public void save() throws Exception {
        String projectKey = "income_consultation";

        String unionKey = "TSN0000000001-platform->team";

        Map<String, Object> data = new HashMap<>();
        data.put(Transaction.UNION_KEY, unionKey);

        transactionService.save(projectKey, "1920021983", "2016101200000001", BigDecimal.valueOf(1.0), "", data);

    }

}