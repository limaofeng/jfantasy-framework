package org.jfantasy.pay.service;

import org.hibernate.Session;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.lucene.dao.hibernate.OpenSessionUtils;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.PayServerApplication;
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
    public void modifyData() {
        List<PropertyFilter> filters = new ArrayList<>();
        Pager<Transaction> pager = new Pager<>();
        do {
            pager = transactionService.findPager(pager, filters);
            for (Transaction transaction : pager.getPageItems()) {
                if (Project.PAYMENT.equals(transaction.getProject().getKey()) || Project.REFUND.equals(transaction.getProject().getKey())) {
                    //添加 subject
                    if (StringUtil.isBlank(transaction.getSubject())) {
                        Session session = OpenSessionUtils.openSession();
                        try {
                            transactionDao.batchSQLExecute("update pay_transaction set subject = ? where sn = ?", OrderKey.newInstance(transaction.get("order_key")).getType(), transaction.getSn());
                        } finally {
                            OpenSessionUtils.closeSession(session);
                        }
                    }
                }
            }
            pager.setCurrentPage(pager.getCurrentPage() + 1);
            pager.setFirst(0);
        } while (pager.getCurrentPage() != pager.getTotalPage());
    }

    @Test
    public void save() throws Exception {
        String projectKey = "income_consultation";

        String unionKey = "TSN0000000001-platform->team";

        Map<String, String> data = new HashMap<>();
        data.put(Transaction.UNION_KEY, unionKey);

        transactionService.save(projectKey, "1920021983", "2016101200000001", BigDecimal.valueOf(1.0), "", data);

    }

}