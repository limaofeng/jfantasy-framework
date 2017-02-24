package org.jfantasy.order;

import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.rpc.client.NettyClientFactory;
import org.jfantasy.rpc.config.NettyClientSettings;
import org.jfantasy.rpc.proxy.RpcProxyFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class OrderServiceByClient implements OrderDetailService, InitializingBean {

    private final NettyClientSettings nettyClientSettings;

    private OrderDetailService orderServiceProxy;

    @Autowired
    public OrderServiceByClient(NettyClientSettings nettyClientSettings) {
        this.nettyClientSettings = nettyClientSettings;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(new NettyClientFactory(nettyClientSettings.getHost(), nettyClientSettings.getPort()));
        orderServiceProxy = rpcProxyFactory.proxyBean(OrderDetailService.class, 10000);
    }

    public OrderDTO save(OrderDTO order) {
        return orderServiceProxy.save(order);
    }

    @Override
    public OrderDTO get(String id) {
        return orderServiceProxy.get(id);
    }

    @Override
    public OrderDTO refund(String id, BigDecimal refundAmount, String note) {
        return orderServiceProxy.refund(id, refundAmount, note);
    }

    @Override
    public void update(String id, OrderDTO order) {
        orderServiceProxy.update(id, order);
    }

    @Override
    public void close(String id) {
        orderServiceProxy.close(id);
    }

    @Override
    public void complete(String id) {
        orderServiceProxy.complete(id);
    }

}
