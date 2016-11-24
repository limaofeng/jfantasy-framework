package org.jfantasy.order.boot;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.order.OrderServiceBuilder;
import org.jfantasy.order.OrderServiceFactory;
import org.jfantasy.order.bean.OrderServer;
import org.jfantasy.order.service.OrderServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class OrderServiceCommandLineRunner implements CommandLineRunner {

    private OrderServiceFactory orderServiceFactory;
    private OrderServerService orderServerService;
    private OrderServiceBuilder builder;

    @Override
    public void run(String... args) throws Exception {
        for (OrderServer entity : orderServerService.find(Restrictions.eq("enabled", true))) {
            orderServiceFactory.register(entity.getType(), builder.build(entity.getProperties()));
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

    @Autowired
    public void setBuilder(OrderServiceBuilder builder) {
        this.builder = builder;
    }

}
