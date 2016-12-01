package org.jfantasy.order.bean;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;


public class OrderTargetKey {

    public static OrderTargetKey newInstance(String type, String sn) {
        return new OrderTargetKey(type, sn);
    }

    public static OrderTargetKey newInstance(String key) {
        String[] ar = key.split(":");
        return new OrderTargetKey(ar[0],ar[1]);
    }

    /**
     * 业务订单类型
     */
    private String type;
    /**
     * 业务订单编号
     */
    private String sn;

    public OrderTargetKey() {
    }

    public OrderTargetKey(String type, String sn) {
        super();
        this.type = type;
        this.sn = sn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().appendSuper(super.hashCode()).append(this.getType()).append(this.getSn()).toHashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof OrderTargetKey) {
            OrderTargetKey key = (OrderTargetKey) o;
            return new EqualsBuilder().appendSuper(super.equals(o)).append(this.getType(), key.getType()).append(this.getSn(), key.getSn()).isEquals();
        }
        return false;
    }

    @Override
    public String toString() {
        return this.type + ":" + this.sn;
    }

}
