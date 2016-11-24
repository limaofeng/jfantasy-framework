package org.jfantasy.order;

import org.jfantasy.order.entity.Order;
import org.jfantasy.rpc.client.NettyClientFactory;
import org.jfantasy.rpc.config.NettyClientSettings;
import org.jfantasy.rpc.proxy.RpcProxyFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderServiceByClient implements OrderService, InitializingBean {

    @Autowired
    private NettyClientSettings nettyClientSettings;

    private OrderService orderServiceProxy;

    @Override
    public void afterPropertiesSet() throws Exception {
        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(new NettyClientFactory(nettyClientSettings.getHost(), nettyClientSettings.getPort()));
        orderServiceProxy = rpcProxyFactory.proxyBean(OrderService.class, 10000);
    }

    public void save(Order order) {
        orderServiceProxy.save(order);
    }

    @Override
    public Order get(String type, String sn) {
        return orderServiceProxy.get(type, sn);
    }

    @Override
    public Order refund(String type, String sn, BigDecimal refundAmount, String note) {
        return orderServiceProxy.refund(type, sn, refundAmount, note);
    }

    @Override
    public void close(String type, String sn) {
        orderServiceProxy.close(type, sn);
    }

}
