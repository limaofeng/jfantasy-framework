package org.jfantasy.order.service.vo;

import org.jfantasy.common.Area;
import org.jfantasy.framework.dao.BaseBusEntity;

/**
 * 收货地址信息
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-16 下午4:16:02
 */
public class Receiver extends BaseBusEntity {

    private static final long serialVersionUID = 851367820092125804L;

    private Long id;

    /**
     * 收货人姓名
     */
    private String name;
    /**
     * 地区存储
     */
    private Area area;
    /**
     * 收货地址
     */
    private String address;
    /**
     * 邮政编码
     */
    private String zipCode;
    /**
     * 手机
     */
    private String mobile;
    /**
     * 是否为默认地址
     */
    private Boolean isDefault = false;// 是否默认

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * 获取地区
     *
     * @return 地区信息
     */
    public Area getArea() {
        return this.area;
    }

    /**
     * 设置地区
     *
     * @param area 地区
     */
    public void setArea(Area area) {
        this.area = area;
    }

}
