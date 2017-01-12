package org.jfantasy.order.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.order.bean.Order;
import org.jfantasy.order.bean.OrderCashFlow;
import org.jfantasy.order.bean.OrderPayee;
import org.jfantasy.order.bean.OrderPrice;
import org.jfantasy.order.bean.enums.DataType;
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
import org.springframework.transaction.annotation.Transactional;


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
        cashFlow.setPayee(orderTypeService.getPayee("registration","clinic"));
        cashFlow.setNotes("到诊所账");
        cashFlow.setOrderType(orderTypeService.get("registration"));
        orderTypeService.save(cashFlow);
    }

    @Test
    public void updateCashFlow() throws Exception {
        OrderCashFlow old = orderTypeService.cashFlow("registration","toclinic");
        OrderCashFlow cashFlow = new OrderCashFlow();
        cashFlow.setCode("toclinic");
        cashFlow.setName("诊所收益");
        cashFlow.setStage(Stage.finished);
        cashFlow.setProject(Project.INCOME);
        cashFlow.setValue(" total ");
        cashFlow.setPayee(orderTypeService.getPayee("registration","clinic"));
        cashFlow.setNotes("到诊所账");
        cashFlow.setOrderType(orderTypeService.get("registration"));
        orderTypeService.update("registration", old.getId(), cashFlow);
    }

    @Test
    @Transactional
    public void cashFlow() throws Exception {
        Order order = orderService.get("2017011000816");
        OrderCashFlow cashFlow = orderTypeService.cashFlow("medicine","doctor");
        LOG.debug("value = " + cashFlow.getValue(order));
        LOG.debug("payee = " + cashFlow.getPayee(order));

        cashFlow = orderTypeService.cashFlow("medicine","pharmacy");
        LOG.debug("value = " + cashFlow.getValue(order));
        LOG.debug("payee = " + cashFlow.getPayee(order));
    }


    @Test
    public void savePrice() throws Exception {
        // 常用费用
        OrderPrice logisticFee = new OrderPrice();
        logisticFee.setDataType(DataType.share);
        logisticFee.setCode("logistic_fee");
        logisticFee.setTitle("物流费");
        orderTypeService.save(logisticFee);
        // 预约
        OrderPrice registrationFee = new OrderPrice();
        registrationFee.setDataType(DataType.exclusive);
        registrationFee.setCode("registration_fee");
        registrationFee.setTitle("预约费");
        registrationFee.setOrderType(orderTypeService.get("registration"));
        orderTypeService.save(registrationFee);
        // 咨询
        OrderPrice consultingFee = new OrderPrice();
        consultingFee.setDataType(DataType.exclusive);
        consultingFee.setCode("consulting_fee");
        consultingFee.setTitle("咨询费");
        consultingFee.setOrderType(orderTypeService.get("consulting"));
        orderTypeService.save(consultingFee);
        // 药单 - 药费
        OrderPrice medicineFee = new OrderPrice();
        medicineFee.setDataType(DataType.exclusive);
        medicineFee.setCode("medicine_Fee");
        medicineFee.setTitle("药费");
        medicineFee.setOrderType(orderTypeService.get("medicine"));
        orderTypeService.save(medicineFee);
        // 药单 - 代煎费
        OrderPrice decoctionFee = new OrderPrice();
        decoctionFee.setDataType(DataType.exclusive);
        decoctionFee.setCode("decoction_fee");
        decoctionFee.setTitle("代煎费");
        decoctionFee.setOrderType(orderTypeService.get("medicine"));
        orderTypeService.save(decoctionFee);
        // 药单 - 加工费
        OrderPrice processFee = new OrderPrice();
        processFee.setDataType(DataType.exclusive);
        processFee.setCode("process_fee");
        processFee.setTitle("加工费");
        processFee.setOrderType(orderTypeService.get("medicine"));
        orderTypeService.save(processFee);
        // 药单 - 包装费
        OrderPrice packFee = new OrderPrice();
        packFee.setDataType(DataType.exclusive);
        packFee.setCode("pack_fee");
        packFee.setTitle("包装费");
        packFee.setOrderType(orderTypeService.get("medicine"));
        orderTypeService.save(packFee);
        // 药单 - 物流费
        OrderPrice mlogisticFee = new OrderPrice();
        mlogisticFee.setDataType(DataType.reference);
        mlogisticFee.setReference(logisticFee);
        mlogisticFee.setOrderType(orderTypeService.get("medicine"));
        orderTypeService.save(mlogisticFee);
    }

    @Test
    public void savePayee() throws Exception {
        // 常用收款人 - 收益人账户
        OrderPayee receiptor = new OrderPayee();
        receiptor.setDataType(DataType.share);
        receiptor.setType(PayeeType.fixed);
        receiptor.setCode("2017000000000001");
        receiptor.setTitle("固定收益人");
        orderTypeService.save(receiptor);
        // 常用收款人 - 医生
        OrderPayee doctor = new OrderPayee();
        doctor.setDataType(DataType.share);
        doctor.setType(PayeeType.member);
        doctor.setCode("doctor");
        doctor.setTitle("医生");
        orderTypeService.save(doctor);
        // 常用收款人 - 诊所
        OrderPayee clinic = new OrderPayee();
        clinic.setDataType(DataType.share);
        clinic.setType(PayeeType.member);
        clinic.setCode("clinic");
        clinic.setTitle("诊所");
        orderTypeService.save(clinic);
        // 常用收款人 - 药房
        OrderPayee pharmacy = new OrderPayee();
        pharmacy.setDataType(DataType.share);
        pharmacy.setType(PayeeType.member);
        pharmacy.setCode("pharmacy");
        pharmacy.setTitle("药房");
        orderTypeService.save(pharmacy);

        // 预约
        orderTypeService.save("registration", new OrderPayee(doctor), new OrderPayee(clinic));
        // 咨询
        orderTypeService.save("consulting", new OrderPayee(doctor), new OrderPayee(clinic));
        // 药单 - 药费
        orderTypeService.save("medicine", new OrderPayee(doctor), new OrderPayee(clinic), new OrderPayee(receiptor));
    }

    @Test
    public void cashflows() throws Exception {

    }

}