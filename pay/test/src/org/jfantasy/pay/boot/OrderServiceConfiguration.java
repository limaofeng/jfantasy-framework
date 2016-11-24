package org.jfantasy.pay.boot;

import org.jfantasy.order.OrderServiceFactory;
import org.jfantasy.order.TestOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

//@Component
public class OrderServiceConfiguration implements CommandLineRunner {

    @Autowired
    private OrderServiceFactory orderServiceFactory;

    @Override
    public void run(String... args) throws Exception {
        orderServiceFactory.register("test",new TestOrderService());
    }

}
