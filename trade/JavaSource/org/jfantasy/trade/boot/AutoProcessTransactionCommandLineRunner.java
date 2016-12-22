package org.jfantasy.trade.boot;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AutoProcessTransactionCommandLineRunner implements CommandLineRunner {

    private TransactionService transactionService;

    @Override
    public void run(String... args) throws Exception {
        for (Transaction transaction : this.transactionService.find(
                Restrictions.in("project", new String[]{
                        ProjectType.withdraw.name(),
                        ProjectType.transfer.name(),
                        ProjectType.deposit.name()}),
                Restrictions.eq("status", TxStatus.unprocessed),
                Restrictions.eq("flowStatus", 0))) {
            this.transactionService.process(transaction.getSn());
        }
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

}
