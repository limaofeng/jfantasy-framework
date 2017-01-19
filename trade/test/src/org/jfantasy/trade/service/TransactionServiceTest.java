package org.jfantasy.trade.service;

import org.jfantasy.pay.PayServerApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@ActiveProfiles("dev")
public class TransactionServiceTest {

    @Autowired
    private TransactionService transactionService;

    @Test
    public void handle() throws Exception {
        transactionService.handleAllowFailure("2017011900038","");
    }

}