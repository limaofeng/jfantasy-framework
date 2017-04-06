package org.jfantasy.order.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.util.common.NumberUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderItem;
import org.jfantasy.order.bean.OrderTargetKey;
import org.jfantasy.order.entity.enums.OrderStatus;
import org.jfantasy.order.rest.models.ProfitChain;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.rest.PaymentController;
import org.jfantasy.pay.rest.RefundController;
import org.jfantasy.pay.rest.models.OrderStatusForm;
import org.jfantasy.pay.rest.models.OrderTransaction;
import org.jfantasy.pay.rest.models.assembler.OrderResourceAssembler;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.trade.bean.Transaction;
import org.jfantasy.trade.rest.TransactionController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.math.BigDecimal;
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

    private static final Log LOG = LogFactory.getLog(OrderController.class);
    public static final OrderResourceAssembler assembler = new OrderResourceAssembler();

    private OrderService orderService;
    private PayService payService;

    private PaymentController paymentController;
    private RefundController refundController;
    private TransactionController transactionController;

    @Autowired
    public OrderController(OrderService orderService, PayService payService) {
        this.orderService = orderService;
        this.payService = payService;
    }

    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Order.class, name = {"refunds", "items", "payments"}),
            allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "name"})
    )
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Order> pager, List<PropertyFilter> filters) {
        if (!pager.isOrderBySetted()) {
            pager.setOrderBy(Order.FIELDS_BY_CREATE_TIME);
            pager.setOrder(Pager.SORT_DESC);
        }
        return assembler.toResources(orderService.findPager(pager, filters));
    }

    /**
     * 获取订单信息
     *
     * @param id 订单KEY
     * @return Order
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Order.class, name = {"refunds", "items", "payments"}),
            allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "name"})
    )
    @RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String id, @RequestParam(name = "fetch", required = false) String fetch) {
        try {
            Order order = this.orderService.get(id, "wait".equals(fetch));
            if (order == null) {
                throw new NotFoundException("[ID=" + id + "]的订单不存在");
            }
            return assembler.toResource(order);
        } catch (PayException e) {
            LOG.error(e.getMessage(), e);
            throw new ValidationException(e.getMessage());
        }
    }

    /**
     * 订单详情
     *
     * @param id 订单ID
     * @return ModelAndView
     */
    @GetMapping("/{id}/details")
    public ModelAndView details(@PathVariable("id") String id) {
        Order order = get(id);
        if (order == null) {
            throw new NotFoundException("[ID=" + id + "]的订单不存在");
        }
        return new ModelAndView("redirect:" + order.getRedirectUrl());
    }

    /**
     * 创建订单交易 - 该接口会判断交易是否创建,如果没有交易记录会添加交易订单到交易记录
     *
     * @param id   订单 id
     * @param form 交易类型
     * @return Transaction
     */
    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms", "default", "disabled"}))
    @RequestMapping(value = "/{id}/transactions", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport transaction(@PathVariable("id") String id, @RequestBody OrderTransaction form) {
        Order order = get(id);// 订单
        if (NumberUtil.isEquals(BigDecimal.ZERO, order.getTotalAmount())) {// 0 元订单，不需要支付
            return assembler.toResource(order);
        }
        // 判断交易类型
        if (form.getType() == OrderTransaction.Type.payment) {
            return transactionController.transform(orderService.payment(order.getId()).getPaymentTransaction());
        } else {
            return TransactionController.assembler.toResource(orderService.refund(id, form.getAmount(), "").getRefundTransaction());
        }
    }

    @JsonResultFilter(allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name", "platforms", "default", "disabled"}))
    @RequestMapping(value = "/{id}/transactions", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Transaction> transactions(@PathVariable("id") String id) {
        Order order = get(id);
        Map<String, Transaction> transactions = new HashMap<>();
        transactions.put("payment", order.getPaymentTransaction());
        transactions.put("refund", order.getRefundTransaction());
        return transactions;
    }

    /**
     * 获取订单信息的付款信息
     *
     * @param id orderKey
     * @return Pager<Payment>
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Payment.class, name = {"pay_config", "orderKey"}),
            allow = @AllowProperty(pojo = Order.class, name = {"key", "subject"})
    )
    @RequestMapping(value = "/{id}/payments", method = RequestMethod.GET)
    @ResponseBody
    public Pager<ResultResourceSupport> payments(@PathVariable("id") String id) {
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("EQS_order.id", id));
        return paymentController.search(new Pager<>(), filters);
    }

    /**
     * 关闭订单
     *
     * @param id
     * @param form
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Order.class, name = {"refunds", "orderItems", "payments"}),
            allow = @AllowProperty(pojo = PayConfig.class, name = {"id", "name"})
    )
    @RequestMapping(value = "/{id}/status", method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void status(@PathVariable("id") String id, @Validated @RequestBody OrderStatusForm form) {
        if (form.getStatus() == OrderStatus.closed) {
            orderService.close(id);
        } else {
            throw new RestException("暂时只支持，订单关闭操作");
        }
    }

    /**
     * 获取订单信息的付款信息
     *
     * @param id orderKey
     * @return Pager<Refund>
     */
    @JsonResultFilter(ignore = @IgnoreProperty(pojo = Refund.class, name = {"order", "pay_config", "payment"}))
    @RequestMapping(value = "/{id}/refunds", method = RequestMethod.GET)
    @ResponseBody
    public Pager<ResultResourceSupport> refunds(@PathVariable("id") String id) {
        List<PropertyFilter> filters = new ArrayList<>();
        filters.add(new PropertyFilter("EQS_order.id", id));
        return refundController.search(new Pager<>(), filters);
    }

    /**
     * 获取订单信息的明细信息
     **/
    @RequestMapping(value = "/{id}/items", method = RequestMethod.GET)
    @ResponseBody
    public List<OrderItem> items(@PathVariable("id") String id) {
        return get(id).getItems();
    }

    /**
     * 执行现金流
     *
     * @param id ID
     * @return List<ProfitChain>
     */
    @RequestMapping(value = "/{id}/cashflows", method = RequestMethod.POST)
    @ResponseBody
    public List<ProfitChain> cashflows(@PathVariable("id") String id) {
        return this.orderService.cashflow(id);
    }

    /**
     * 获取订单信息的利润链
     **/
    @RequestMapping(value = "/{id}/cashflows", method = RequestMethod.GET)
    @ResponseBody
    public List<ProfitChain> profitChainsrofitChain(@PathVariable("id") String id) {
        return this.orderService.profitChains(id);
    }

    private Order get(String id) {
        return id.contains(":") ? orderService.get(OrderTargetKey.newInstance(id)) : orderService.get(id);
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
