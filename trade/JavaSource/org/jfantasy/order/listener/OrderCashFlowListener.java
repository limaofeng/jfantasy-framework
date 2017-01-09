package org.jfantasy.order.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderCashFlow;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.event.OrderStatusChangedEvent;
import org.jfantasy.order.service.OrderTypeService;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OrderCashFlowListener implements ApplicationListener<OrderStatusChangedEvent> {

    private static final Log LOG = LogFactory.getLog(OrderCashFlowListener.class);

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
        List<OrderCashFlow> cashFlows = orderTypeService.cashflows(order.getType(), Stage.finished);
        // 设置初始值
        order.setTotal(order.getTotalAmount());
        order.setSurplus(order.getTotalAmount());
        for (OrderCashFlow cashFlow : cashFlows) {
            BigDecimal surplus = order.getSurplus();
            order.setTotal(order.getTotalAmount());
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
        BigDecimal value = cashFlow.getValue(order);
        if (BigDecimal.ZERO.setScale(2, RoundingMode.DOWN).equals(value)) {
            LOG.error(" 金额为 0.00，跳过分配规则 > " + cashFlow.getId());
            return value;
        }
        Transaction transaction = transfer(order, cashFlow.getProject(), value, from, payee, order.getId() + "->" + cashFlow.getCode(), cashFlow.getName());
        // 设置初始值
        order.setTotal(transaction.getAmount());
        order.setSurplus(transaction.getAmount());
        for (OrderCashFlow flow : cashFlow.getSubflows()) {
            BigDecimal surplus = order.getSurplus();
            order.setTotal(transaction.getAmount());
            order.setSurplus(surplus.subtract(startupFlow(flow, order, payee)));
        }
        return transaction.getAmount();
    }

    /**
     * 转账接口
     *
     * @param order    订单对象
     * @param project  转账项目
     * @param amount   转账金额
     * @param from     转出账户
     * @param to       转入账户
     * @param unionKey 唯一标示
     * @param notes    备注
     * @return Transaction
     */
    private Transaction transfer(Order order, String project, BigDecimal amount, String from, String to, String unionKey, String notes) {

        Map<String, Object> data = new HashMap<>();
        data.putAll(order.getAttrs());
        data.put(Transaction.UNION_KEY, unionKey);
        data.put(Transaction.ORDER_ID, order.getId());
        data.put(Transaction.ORDER_TYPE, order.getType());

        return transactionService.save(project, from, to, amount, notes, data);
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
