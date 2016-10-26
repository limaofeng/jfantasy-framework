package org.jfantasy.pay.service;

import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.pay.bean.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void save() throws Exception {
        String projectKey = "income_consultation";

        String unionKey = "TSN0000000001-platform->team";

        Map<String, String> data = new HashMap<>();
        data.put(Transaction.UNION_KEY, unionKey);

        transactionService.save(projectKey, "1920021983", "2016101200000001", BigDecimal.valueOf(1.0), "", data);

    }

}