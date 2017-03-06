package org.jfantasy.order;

import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.rpc.config.NettyClientSettings;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class OrderServiceByClientTest {

    private static OrderDetailService orderService;

    @Before
    public void setUp() throws Exception {
        NettyClientSettings settings = new NettyClientSettings();
        settings.setHost("localhost");
        settings.setPort(9090);
        OrderServiceByClient client = new OrderServiceByClient(settings);
        client.afterPropertiesSet();
        orderService = client;

    }

    @Test
    public void get() throws Exception {
        OrderDTO dto = orderService.get("2016121200352");
        Assert.assertNotNull(dto);
    }

}