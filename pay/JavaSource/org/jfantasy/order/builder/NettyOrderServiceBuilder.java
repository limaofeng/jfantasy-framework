package org.jfantasy.order.builder;


import org.jfantasy.order.OrderService;
import org.jfantasy.order.OrderServiceBuilder;
import org.jfantasy.order.bean.OrderServer;
import org.jfantasy.rpc.client.NettyClientFactory;
import org.jfantasy.rpc.proxy.RpcProxyFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class NettyOrderServiceBuilder implements OrderServiceBuilder {

    private static final long TIMEOUT_IN_MILLIS = 10000;

    @Override
    public OrderService build(Map props) {
        String host = props.get(OrderServer.PROPS_HOST).toString();
        String port = props.get(OrderServer.PROPS_PORT).toString();
        RpcProxyFactory rpcProxyFactory = new RpcProxyFactory(new NettyClientFactory(host, Integer.valueOf(port)));
        return rpcProxyFactory.proxyBean(OrderService.class, TIMEOUT_IN_MILLIS);
    }

}
