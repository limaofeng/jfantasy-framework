package org.jfantasy.order.service;

import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.order.OrderDetailService;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.rpc.annotation.ServiceExporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@ServiceExporter(value = "orderDetailService", targetInterface = OrderDetailService.class)
public class OrderDetailServiceImpl implements OrderDetailService{

    private OrderService orderService;
    private OrderTypeService orderTypeService;

    @Override
    public OrderDTO save(OrderDTO zorder) {
        Order order = this.orderService.findUnique(zorder.getType(), zorder.getSn());
        if (order != null) {
            zorder.setId(order.getId());
            return zorder;
        }
        OrderType orderType = this.orderTypeService.get(zorder.getType());
        if (orderType == null || !orderType.getEnabled()) {
            throw new ValidationException("支付系统不能处理该类型的订单[" + zorder.getType() + "]，请检查或者联系开发人员!");
        }
        //保存订单信息
        order = this.orderService.submitOrder(zorder);
        zorder.setId(order.getId());
        return zorder;
    }

    @Override
    @Transactional
    public OrderDTO get(String id) {
        Order order = this.orderService.get(id);
        if(order == null) {
            return null;
        }
        OrderDTO dto = new OrderDTO();
        return BeanUtil.copyProperties(dto,order);
    }

    @Override
    @Transactional
    public OrderDTO refund(String id, BigDecimal refundAmount, String note) {
        Order order = this.orderService.refund(id,refundAmount,note);
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setRefundAmount(order.getRefundAmount());
        return dto;
    }

    @Override
    @Transactional
    public void update(String id, OrderDTO dto) {
        Order order = this.orderService.get(id);
        order.getAttrs().putAll(dto.getAttrs());
        this.orderService.update(order);
    }

    @Override
    public void close(String id) {
        this.orderService.close(id);
    }

    @Override
    public void complete(String id) {
        this.orderService.complete(id);
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setOrderTypeService(OrderTypeService orderTypeService) {
        this.orderTypeService = orderTypeService;
    }

}
