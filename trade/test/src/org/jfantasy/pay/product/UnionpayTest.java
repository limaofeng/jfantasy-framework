package org.jfantasy.pay.product;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.service.PayConfigService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.Properties;

/**
 * 银联支付测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UnionpayTest {

    private final static Log LOG = LogFactory.getLog(UnionpayTest.class);

    private Unionpay unionpay = new Unionpay();
    @Autowired
    private PayConfigService payConfigService;
    private Order order = new Order();
    private PayConfig payConfig;

    @Before
    public void setUp() throws Exception {
        // 银联支付 1
            //上传签名证书
            //FileDetail signCert = fileUploadService.upload(new File(PathUtil.classes() + "/certs/app.pfx"), "/certs/app.pfx", "haolue-upload");
            //上传验签证书
            //FileDetail validateCert = fileUploadService.upload(new File(PathUtil.classes() + "/certs/acp20151027.cer"), "/certs/acp20151027.cer", "haolue-upload");
            //保存支付配置
            PayConfig payConfig = payConfigService.findUnique(Restrictions.eq("payProductId", "unionpay"), Restrictions.eq("bargainorId", "802310073920500"));
            if (payConfig == null) {
                payConfig = new PayConfig();
            }
            payConfig.setName("正本上工银联支付");
            payConfig.setPayConfigType(PayConfig.PayConfigType.online);
            payConfig.setPayProductId("unionpay");
            payConfig.setPayFeeType(PayConfig.PayFeeType.fixed);
            payConfig.setPayFee(BigDecimal.ZERO);
            payConfig.setBargainorId("802310073920500");
            payConfig.setBargainorKey("201601");
//            payConfig.setSignCert(signCert);
//            payConfig.setValidateCert(validateCert);
            payConfigService.save(this.payConfig = payConfig);
            unionpay.setDeployStatus(DeployStatus.Production);
//        this.payConfig = payConfigService.findUnique(Restrictions.eq("payProductId", "unionpay"), Restrictions.eq("bargainorId", "777290058123224"));
//        unionpay.setDeployStatus(DeployStatus.Develop);

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testApp() throws Exception {

        Payment payment = new Payment();
        payment.setSn("T00000007");
        payment.setPayConfig(this.payConfig);
        payment.setTotalAmount(BigDecimal.valueOf(0.01));
        payment.setCreateTime(DateUtil.parse("20160128131252","yyyyMMddHHmmss"));

        Object result = unionpay.app(payment,order,new Properties());

        LOG.debug(result);

    }

    @Test
    public void testQuery() throws Exception {
        Payment payment = new Payment();
        payment.setSn("P2016030700363");
        payment.setPayConfig(this.payConfig);
        payment.setTotalAmount(BigDecimal.valueOf(0.01));
        payment.setCreateTime(DateUtil.parse("20160307143026","yyyyMMddHHmmss"));

        PaymentStatus result = unionpay.query(payment);

        LOG.debug(result);
    }

    @Test
    public void testPayNotify() throws Exception {
        String body = "{\"sign\":\"DQExtgJ/HFkvz7rMEGDKdIOS+jz7wsZ7Hwvc4iYv+hYIe4Bx5f+sMfizqaeiA64iTWeyzZ1XIERcbKcQQw7GfeCVtqfNW8HGx2bsKk7iEUrJK1Qh1kJE4xqCxrJCatXGMTFBe5PgBxSYi96d8oS/jdJe2vAo5+aOYgHRKNFqw8amgX77Dxv5FQzorviuCsW1G6NuiTNtOQBllSlVxVf61JCFH9J6qpiWibk+wKXWceuenrJpiucnxTrarh1dMpbsQlocjn/kDqP6lTleoHwWAQ6YR//fIcbDDSEIIQPeO2QkIiSKvq7BnWygunQMymmTcVKJ0XS5cb8KAttPxpbeQQ==\",\"data\":\"pay_result=success&tn=201601291406021626798&cert_id=69597475696\"}";

        Payment payment = new Payment();
        payment.setSn("T00000006");
        payment.setPayConfig(this.payConfig);
        payment.setTotalAmount(BigDecimal.valueOf(0.01));
        payment.setCreateTime(DateUtil.parse("20160128131252","yyyyMMddHHmmss"));

        unionpay.payNotify(payment,body);

        LOG.debug(payment);

        body = "txnType=01&respCode=00&currencyCode=156&merId=802310073920500&settleDate=0129&txnSubType=01&version=5.0.0&txnAmt=2&signMethod=01&certId=69597475696&settleAmt=2&traceTime=0129143436&encoding=UTF-8&settleCurrencyCode=156&bizType=000201&respMsg=Success%21&traceNo=460833&queryId=201601291434364608338&signature=Rs49SebZD2EbxB1A6UhzZ4ITpLv6SZ3jGzrIOYHMurLEWVndaEsmUU5%2BDvdg6AIYz8QE0D4Yg2lldHaN0NEXC8jLYY%2FdYPoJmWDBK5mzuGTbKA9ZtIblBGzLZncszyNgjvZ90dYJ167vwu7iQeMCJpq7Lj7WNp%2BFDDcOTr5nKihOsvl64i%2FIi4hYZQs9bRBIoP%2Bsn%2FtqN%2F4kQ21WoG7yqFmdqz9xE7SkZjI6eNhdAB7ol219S1BtwfM1fvxo8N76pR0QDldldIQm4Yq59qqIgPxuNcnbMqLlqDWbIsHVoOVTSgPmLMa5aj46BiXgYBY88RlpFA3w5QoPAQL00ZrMoA%3D%3D&orderId=P2016012900166&txnTime=20160129143436&accessType=0";

        unionpay.payNotify(payment,body);

        LOG.debug(payment);

    }
}