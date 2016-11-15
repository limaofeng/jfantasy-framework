package org.jfantasy.pay.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.pay.bean.*;
import org.jfantasy.pay.order.entity.OrderItem;
import org.jfantasy.pay.order.entity.OrderKey;
import org.jfantasy.pay.rest.models.OrderStatusForm;
import org.jfantasy.pay.rest.models.OrderTransaction;
import org.jfantasy.pay.rest.models.assembler.OrderResourceAssembler;
import org.jfantasy.pay.service.AccountService;
import org.jfantasy.pay.service.OrderService;
import org.jfantasy.pay.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单
 **/
@RestController
@RequestMapping("/orders")
public class OrderController {

    private OrderResourceAssembler assembler = new OrderResourceAssembler();

    private OrderService orderService;
    private AccountService accountService;
    private TransactionService transactionService;
    private PaymentController paymentController;
    private RefundController refundController;
    private TransactionController transactionController;

    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Order.class, name = {"refunds", "orderItems", "payments"}),
            allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "name"})
    )
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Order> pager, List<PropertyFilter> filters) {
        return assembler.toResources(orderService.findPager(pager, filters));
    }

    /**
     * 获取订单信息
     *
     * @param key 订单KEY
     * @return Order
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Order.class, name = {"refunds", "orderItems", "payments"}),
            allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "name"})
    )
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String key) {
        Order order = orderService.get(OrderKey.newInstance(key));
        if (order == null) {
            throw new NotFoundException("[ID=" + key + "]的订单不存在");
        }
        return assembler.toResource(order);
    }

    /**
     * 创建订单交易 - 该接口会判断交易是否创建,如果没有交易记录会添加交易订单到交易记录
     *
     * @param key              订单 key
     * @param orderTransaction 交易类型
     * @return Transaction
     */
    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms", "default", "disabled"}))
    @RequestMapping(value = "/{id}/transactions", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport transaction(@PathVariable("id") String key, @RequestBody OrderTransaction orderTransaction) {
        Map<String, Object> data = new HashMap<>();
        // 判断交易类型
        if (orderTransaction.getType() == OrderTransaction.Type.payment) {
            // 订单
            Order order = orderService.getOrder(OrderKey.newInstance(key));
            // 保存到交易表的数据
            data.putAll(order.getProperties());
            data.put(Transaction.ORDER_KEY, key);
            data.put(Transaction.ORDER_SUBJECT, order.getSubject());
            data.put("order_time",order.getOrderTime());
            String from = accountService.findUniqueByCurrentUser().getSn();// 付款方
            return transactionController.transform(this.transactionService.payment(from, order.getPayableFee(), "", data));
        } else {
            // 订单
            Order order = orderService.get(OrderKey.newInstance(key));
            // 保存到交易表的数据
            data.putAll(order.getProperties());
            data.put(Transaction.ORDER_KEY, key);
            data.put(Transaction.ORDER_SUBJECT, order.getSubject());
            data.put("order_time",order.getOrderTime());
            return TransactionController.assembler.toResource(this.transactionService.refund(order.getKey(), orderTransaction.getAmount(), ""));
        }
    }

    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms", "default", "disabled"}))
    @RequestMapping(value = "/{id}/transactions", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public List<ResultResourceSupport> transactions(@PathVariable("id") String key, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("INS_unionId", Transaction.generateUnionid(OrderTransaction.Type.payment.getValue(), key), Transaction.generateUnionid(OrderTransaction.Type.refund.getValue(), key)));
        return transactionController.seach(new Pager<>(), filters).getPageItems();
    }

    /**
     * 获取订单信息的付款信息
     *
     * @param id orderKey
     * @return Pager<Payment>
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Payment.class, name = {"payConfig", "orderKey"}),
            allow = @AllowProperty(pojo = Order.class, name = {"key", "subject"})
    )
    @RequestMapping(value = "/{id}/payments", method = RequestMethod.GET)
    @ResponseBody
    public Pager<ResultResourceSupport> payments(@PathVariable("id") String id) {
        OrderKey key = OrderKey.newInstance(id);
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("EQS_order.type", key.getType()));
        filters.add(new PropertyFilter("EQS_order.sn", key.getSn()));
        return paymentController.search(new Pager<>(), filters);
    }

    @JsonResultFilter(ignore = @IgnoreProperty(pojo = Refund.class, name = {"order", "payConfig", "payment"}))
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
    @ResponseBody
    public ResultResourceSupport status(@PathVariable("id") String id, @Validated @RequestBody OrderStatusForm form) {
        OrderKey key = OrderKey.newInstance(id);
        switch (form.getStatus()) {
            case close:
                return assembler.toResource(orderService.close(key));
            default:
                throw new RestException("暂时只支持，订单关闭操作");
        }
    }

    /**
     * 获取订单信息的付款信息
     *
     * @param id orderKey
     * @return Pager<Refund>
     */
    @JsonResultFilter(ignore = @IgnoreProperty(pojo = Refund.class, name = {"order", "payConfig", "payment"}))
    @RequestMapping(value = "/{id}/refunds", method = RequestMethod.GET)
    @ResponseBody
    public Pager<ResultResourceSupport> refunds(@PathVariable("id") String id) {
        OrderKey key = OrderKey.newInstance(id);
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("EQS_order.type", key.getType()));
        filters.add(new PropertyFilter("EQS_order.sn", key.getSn()));
        return refundController.search(new Pager<>(), filters);
    }

    /**
     * 获取订单信息的明细信息
     **/
    @RequestMapping(value = "/{id}/items", method = RequestMethod.GET)
    @ResponseBody
    public List<OrderItem> items(@PathVariable("id") String id) {
        return get(id).getOrderItems();
    }

    private Order get(String id) {
        return orderService.get(OrderKey.newInstance(id));
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
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
    public void setPaymentController(PaymentController paymentController) {
        this.paymentController = paymentController;
    }

    @Autowired
    public void setRefundController(RefundController refundController) {
        this.refundController = refundController;
    }

    @Autowired
    public void setTransactionController(TransactionController transactionController) {
        this.transactionController = transactionController;
    }
}
