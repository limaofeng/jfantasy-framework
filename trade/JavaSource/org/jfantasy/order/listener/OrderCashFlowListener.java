package org.jfantasy.order.listener;

import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderCashFlow;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.event.OrderStatusChangedEvent;
import org.jfantasy.order.service.OrderTypeService;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.Project;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderCashFlowListener implements ApplicationListener<OrderStatusChangedEvent> {

    private OrderTypeService orderTypeService;
    private TransactionService transactionService;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Override
    public void onApplicationEvent(OrderStatusChangedEvent event) {
        Order order = event.getOrder();
        if (order.getStatus() != OrderStatus.complete) {
            return;
        }
        Account platform = transactionService.platform();
        List<OrderCashFlow> cashFlows = orderTypeService.cashFlow(order.getType(), Stage.finished);
        for (OrderCashFlow cashFlow : cashFlows) {
            BigDecimal surplus = order.getSurplus();
            order.setSurplus(surplus.subtract(startupFlow(cashFlow, order, platform.getSn())));
        }
    }

    /**
     * 启动流程
     *
     * @param cashFlow 订单现金流
     * @param order    订单
     * @param from     转出账户
     * @return BigDecimal
     */
    private BigDecimal startupFlow(OrderCashFlow cashFlow, Order order, String from) {
        String payee = cashFlow.getPayee(order);
        Transaction transaction = transfer(order, cashFlow.getValue(order), from, payee, order.getId() + "->" + cashFlow.getCode(), cashFlow.getName());
        order.setSurplus(transaction.getAmount());
        for (OrderCashFlow flow : cashFlow.getSubflows()) {
            BigDecimal surplus = order.getSurplus();
            order.setSurplus(surplus.subtract(startupFlow(flow, order, payee)));
        }
        return transaction.getAmount();
    }

    /**
     * 转账接口
     *
     * @param order    订单对象
     * @param amount   转账金额
     * @param from     转出账户
     * @param to       转入账户
     * @param unionKey 唯一标示
     * @param notes    备注
     * @return Transaction
     */
    private Transaction transfer(Order order, BigDecimal amount, String from, String to, String unionKey, String notes) {
        String projectKey = Project.INCOME;

        Map<String, Object> data = new HashMap<>();
        data.putAll(order.getAttrs());
        data.put(Transaction.UNION_KEY, unionKey);
        data.put(Transaction.ORDER_ID, order.getId());
        data.put(Transaction.ORDER_TYPE, order.getType());

        return transactionService.save(projectKey, from, to, amount, notes, data);
    }

    @Autowired
    public void setTransactionService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Autowired
    public void setOrderTypeService(OrderTypeService orderTypeService) {
        this.orderTypeService = orderTypeService;
    }

}
