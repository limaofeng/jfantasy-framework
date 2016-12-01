package org.jfantasy.logistics.rest.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import org.jfantasy.logistics.bean.DeliveryItem;

import java.util.ArrayList;
import java.util.List;

@ApiModel("发货表单")
public class LogisticsForm {
    /**
     * 配送方式
     */
    private Long deliveryTypeId;
    /**
     * 订单编号
     */
    private String orderId;
    /**
     * 物流项
     */
    private List<DeliveryItemForm> items;

    public Long getDeliveryTypeId() {
        return deliveryTypeId;
    }

    public void setDeliveryTypeId(Long deliveryTypeId) {
        this.deliveryTypeId = deliveryTypeId;
    }

    public List<DeliveryItemForm> getItems() {
        return items;
    }

    public void setItems(List<DeliveryItemForm> items) {
        this.items = items;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @JsonIgnore
    public List<DeliveryItem> getDeliveryItems() {
        List<DeliveryItem> deliveryItems = new ArrayList<>();
        for (DeliveryItemForm itemForm : this.getItems()) {
            DeliveryItem deliveryItem = new DeliveryItem();
            deliveryItem.setSn(itemForm.getSn());
            deliveryItem.setQuantity(itemForm.getQuantity());
            deliveryItems.add(deliveryItem);
        }
        return deliveryItems;
    }

}
