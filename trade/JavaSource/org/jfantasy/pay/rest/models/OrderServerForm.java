package org.jfantasy.pay.rest.models;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import org.jfantasy.order.bean.OrderServer;

import java.util.HashMap;
import java.util.Map;

public class OrderServerForm {

    private String orderType;
    private String title;
    private String description;
    /**
     * callType = rpc 时必填
     */
    private String host;
    /**
     * callType = rpc 时必填
     */
    private int port;
    /**
     * callType = restful 时必填
     */
    private String url;
    /**
     * 访问授权的 token
     */
    private String token;

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put(OrderServer.PROPS_HOST, this.host);
        props.put(OrderServer.PROPS_PORT, this.port);
        return props;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
