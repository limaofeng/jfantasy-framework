package org.jfantasy.order.boot;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.order.bean.OrderServer;
import org.jfantasy.order.OrderServiceFactory;
import org.jfantasy.order.TestOrderService;
import org.jfantasy.order.service.OrderServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceCommandLineRunner implements CommandLineRunner {

    private OrderServiceFactory orderServiceFactory;
    private OrderServerService orderServerService;

    @Override
    public void run(String... args) throws Exception {
        orderServiceFactory.register(TestOrderService.ORDER_TYPE,TestOrderService.getInstance());
        for (OrderServer entity : orderServerService.find(Restrictions.eq("enabled", true))) {
            orderServiceFactory.register(entity.getType(), orderServiceFactory.getBuilder(entity.getCallType()).build(entity.getProperties()));
        }
    }

    @Autowired
    public void setOrderServiceFactory(OrderServiceFactory orderServiceFactory) {
        this.orderServiceFactory = orderServiceFactory;
    }

    @Autowired
    public void setOrderServerService(OrderServerService orderServerService) {
        this.orderServerService = orderServerService;
    }
}
