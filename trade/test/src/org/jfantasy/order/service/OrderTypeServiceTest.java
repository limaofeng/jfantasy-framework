package org.jfantasy.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderCashFlow;
import org.jfantasy.order.bean.enums.PayeeType;
import org.jfantasy.order.bean.enums.Stage;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.trade.bean.Project;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@ActiveProfiles("dev")
public class OrderTypeServiceTest {

    private final static Log LOG = LogFactory.getLog(OrderTypeServiceTest.class);

    @Autowired
    private OrderTypeService orderTypeService;
    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void saveCashFlow() throws Exception {
        OrderCashFlow cashFlow = new OrderCashFlow();
        cashFlow.setCode("toclinic");
        cashFlow.setName("诊所收益");
        cashFlow.setStage(Stage.finished);
        cashFlow.setProject(Project.INCOME);
        cashFlow.setValue(" total * 0.3 ");
        cashFlow.setPayeeType(PayeeType.member);
        cashFlow.setPayee(" 14 ");
        cashFlow.setNotes("到诊所账");
        cashFlow.setOrderType(orderTypeService.get("registration"));
        orderTypeService.save(cashFlow);
    }

    @Test
    public void updateCashFlow() throws Exception {
        OrderCashFlow old = orderTypeService.cashFlow("toclinic");
        OrderCashFlow cashFlow = new OrderCashFlow();
        cashFlow.setCode("toclinic");
        cashFlow.setName("诊所收益");
        cashFlow.setStage(Stage.finished);
        cashFlow.setProject(Project.INCOME);
        cashFlow.setValue(" total * 0.3 ");
        cashFlow.setPayeeType(PayeeType.member);
        cashFlow.setPayee(" 14 ");
        cashFlow.setNotes("到诊所账");
        cashFlow.setOrderType(orderTypeService.get("registration"));
        orderTypeService.update("registration",old.getId(),cashFlow);
    }

    @Test
    public void cashFlow() throws Exception {
        Order order = orderService.get("2016061300001");
        OrderCashFlow cashFlow = orderTypeService.cashFlow("toclinic");
        LOG.debug("value = " + cashFlow.getValue(order));
        LOG.debug("payee = " + cashFlow.getPayee(order));
    }

    @Test
    public void getValue() throws Exception {

    }

    @Test
    public void getPayee() throws Exception {

    }

    @Test
    public void cashflows() throws Exception {

    }

}