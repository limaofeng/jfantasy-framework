package org.jfantasy.pay.rest.models;

import org.jfantasy.pay.bean.Order;

import javax.validation.constraints.NotNull;

public class OrderStatusForm {

    @NotNull
    private Order.Status status;

    public Order.Status getStatus() {
        return status;
    }

    public void setStatus(Order.Status status) {
        this.status = status;
    }

}
