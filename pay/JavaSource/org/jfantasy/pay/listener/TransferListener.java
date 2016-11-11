package org.jfantasy.pay.listener;

import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.pay.bean.Project;
import org.jfantasy.pay.bean.Transaction;
import org.jfantasy.pay.bean.enums.ProjectType;
import org.jfantasy.pay.bean.enums.TxStatus;
import org.jfantasy.pay.event.TransactionChangedEvent;
import org.jfantasy.pay.service.AccountService;
import org.jfantasy.pay.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class TransferListener implements ApplicationListener<TransactionChangedEvent> {

    private AccountService accountService;
    private ProjectService projectService;

    private ProjectType[] projectTypes = new ProjectType[]{ProjectType.transfer, ProjectType.card};

    @Override
    public void onApplicationEvent(TransactionChangedEvent event) {
        Transaction transaction = event.getTransaction();
        Project project = this.projectService.get(transaction.getProject());
        if (transaction.getStatus() != TxStatus.unprocessed) {
            return;
        }
        if (!ObjectUtil.exists(projectTypes, project.getType())) {
            return;
        }
        accountService.transfer(transaction.getSn(), "自动转账");
    }

    @Autowired
    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    @Autowired
    public void setProjectService(ProjectService projectService) {
        this.projectService = projectService;
    }

}
