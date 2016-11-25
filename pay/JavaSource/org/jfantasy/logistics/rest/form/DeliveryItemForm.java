package org.jfantasy.logistics.rest.form;

import io.swagger.annotations.ApiModel;

@ApiModel("物流项")
public class DeliveryItemForm {

    /** 货品编号 **/
    private String sn;
    /** 物流数量 **/
    private Integer quantity;

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

}
