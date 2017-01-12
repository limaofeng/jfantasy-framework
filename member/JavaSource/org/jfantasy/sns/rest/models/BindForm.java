package org.jfantasy.sns.rest.models;

import org.jfantasy.sns.bean.enums.PlatformType;

public class BindForm {
    private PlatformType type;
    private String appId;
    private String openId;

    public PlatformType getType() {
        return type;
    }

    public void setType(PlatformType type) {
        this.type = type;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOpenId() {
        return openId;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }
}
