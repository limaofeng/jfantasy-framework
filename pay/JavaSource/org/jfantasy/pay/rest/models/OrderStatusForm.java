package org.jfantasy.pay.rest.models;

import org.jfantasy.pay.order.entity.enums.OrderStatus;

import javax.validation.constraints.NotNull;

public class OrderStatusForm {

    @NotNull
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

}
