
package org.jfantasy.trade.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.ms.EventEmitter;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.ProjectType;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.event.TransactionChangedEvent;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class PublishTransactionAllEventByMNS implements ApplicationListener<TransactionChangedEvent> {

    private static final Log LOGGER = LogFactory.getLog(PublishTransactionAllEventByMNS.class);

    private ProjectService projectService;
    private TransactionService transactionService;

    private TxStatus[] txStatuses = new TxStatus[]{TxStatus.unprocessed, TxStatus.success};

    private EventEmitter eventEmitter;

    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        Project project = this.projectService.get(transaction.getProject());
        // 自动处理 未处理的转账交易
        if (!ObjectUtil.exists(txStatuses, transaction.getStatus())) {
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
        // 发布 trade.* 事件
        if (transaction == null) {
            return;
        }
        if (transaction.getStatus() == TxStatus.unprocessed) {
            if (ProjectType.withdraw == project.getType()) {
                eventEmitter.fireEvent("trade." + project.getType().name(), sn, String.format("%s发起提现请求", transaction.getFrom()), JSON.serialize(transaction));
            }
        } else {
            eventEmitter.fireEvent("trade." + project.getType().name(), sn, String.format("%s交易处理成功", transaction.getSn()), JSON.serialize(transaction));
        }
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }

}
