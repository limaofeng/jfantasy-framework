package org.jfantasy.pay.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.ProjectType;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.event.TransactionChangedEvent;
import org.jfantasy.pay.service.AccountService;
import org.jfantasy.pay.service.ProjectService;
import org.jfantasy.pay.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Component
public class TransferListener implements ApplicationListener<TransactionChangedEvent> {

    private static final Log LOGGER = LogFactory.getLog(TransferListener.class);

    private AccountService accountService;
    private ProjectService projectService;
    private TransactionService transactionService;

    private ProjectType[] projectTypes = new ProjectType[]{ProjectType.transfer, ProjectType.card};

    @Async
    @Override
    @Transactional
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        Project project = this.projectService.get(transaction.getProject());
        // 自动处理 未处理的转账交易
        if (transaction.getStatus() != TxStatus.unprocessed || !ObjectUtil.exists(projectTypes, project.getType())) {
            return;
        }
        // 防止数据库事物未提交，造成数据未添加到数据库中
        String sn = transaction.getSn();
        try {
            do {
                Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                transaction = this.transactionService.get(sn);
            } while (transaction == null);
        } catch (InterruptedException e) {
            LOGGER.debug("Interrupted!", e);
            Thread.currentThread().interrupt();
        }
        // 执行转账操作
        accountService.transfer(sn, "自动转账");
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

}
