package org.jfantasy.pay.product;

import org.jfantasy.order.bean.Order;
import org.jfantasy.order.service.OrderService;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.service.PayProductConfiguration;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.pay.service.PaymentService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Properties;

/**
 * 微信支付测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
public class WeixinpayTest {

    @Autowired
    private PayProductConfiguration payProductConfiguration;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private PayService payService;
    private Weixinpay weixinpay;

    @Before
    public void setUp() throws Exception {
        weixinpay = payProductConfiguration.loadPayProduct("weixinpay");
    }

    @Test
    @Transactional
    public void app() throws Exception {
        Properties props = new Properties();
        Payment payment = paymentService.get("P2016120900031");
        Order order = orderService.get("2016120900312");
        weixinpay.app(payment, order, props);
    }

    @Test
    @Transactional
    public void query() throws Exception{
        Payment payment = paymentService.get("P2016081500001");
        Map<String, String> map = weixinpay.query(payment);
        System.out.println(map);
    }

    @Test
    public void close() throws Exception{
        //Payment payment = paymentService.get("P2016081500001");
//        Payment payment = new Payment();
//        payment.setSn("P2016081500001");
        payService.close("PDEV2017030600052");
        //weixinpay.close(payment);
    }
}