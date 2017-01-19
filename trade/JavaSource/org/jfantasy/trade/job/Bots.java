package org.jfantasy.trade.job;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.enums.OrderFlow;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.bean.enums.TxStatus;
import org.jfantasy.trade.service.AccountService;
import org.jfantasy.trade.service.ProjectService;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Bots {

    private final TransactionService transactionService;
    private final OrderService orderService;
    private final ProjectService projectService;

    @Autowired
    public Bots(TransactionService transactionService, OrderService orderService, ProjectService projectService) {
        this.transactionService = transactionService;
        this.orderService = orderService;
        this.projectService = projectService;
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void transfer() {
        List<Project> projects = projectService.find(AccountService.PROJECT_TYPES);
        for (Transaction transaction : transactionService.find(
                Restrictions.eq("status", TxStatus.unprocessed),
                Restrictions.eq("flowStatus", 0),
                Restrictions.in("project", ObjectUtil.toFieldArray(projects, "key", String.class)))) {
            transactionService.handleAllowFailure(transaction.getSn(), " 转账机器人 ");
        }
    }

    @Scheduled(fixedDelay = 1000 * 60 * 10)
    public void cashflows() {
        for (Order order : orderService.find(Restrictions.eq("status", OrderStatus.complete), Restrictions.eq("flow", OrderFlow.initial), Restrictions.le("completionTime", DateUtil.addMinutes(DateUtil.now(), -5)))) {
            orderService.cashflow(order.getId());
        }
    }

}
