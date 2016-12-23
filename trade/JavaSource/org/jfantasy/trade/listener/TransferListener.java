package org.jfantasy.trade.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class TransferListener implements ApplicationListener<TransactionChangedEvent> {

    private static final Log LOGGER = LogFactory.getLog(TransferListener.class);

    private static final Lock LOCK = new ReentrantLock();

    private ProjectService projectService;
    private TransactionService transactionService;

    private ProjectType[] projectTypes = new ProjectType[]{ProjectType.withdraw, ProjectType.transfer, ProjectType.deposit};

    @Async
    @Override
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
        try {
            LOCK.lock();
            // 执行转账操作
            transactionService.handle(sn, "后台自动处理");
        } finally {
            LOCK.unlock();
        }
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