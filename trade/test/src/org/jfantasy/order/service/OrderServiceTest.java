package org.jfantasy.order.service;

import org.jfantasy.order.entity.OrderDTO;
import org.jfantasy.order.entity.OrderItemDTO;
import org.jfantasy.pay.PayServerApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@ActiveProfiles("dev")
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void submitOrder() throws Exception {
        OrderDTO order = new OrderDTO();
        order.setSn("0123456789");
        order.setType("medicine");
        order.setMemberId(14L);
        order.setPayableFee(BigDecimal.ZERO);//
        order.setTotalFee(BigDecimal.ZERO);//

        OrderItemDTO item = new OrderItemDTO();
        item.setName("预约");
        item.setSn("");
        item.setProductType("时段");
        item.setProductId("时段ID");
        item.setProductQuantity(0);
        item.setProductPrice(BigDecimal.ZERO);
        item.setProductQuantity(1);

        order.addItem(item);
        orderService.submitOrder(order);
    }

    @Test
    public void cashflow() throws Exception {
        orderService.cashflow("2017011901052");
    }

}