package org.jfantasy.pay.product;

//import com.alipay.api.AlipayClient;
//import com.alipay.api.DefaultAlipayClient;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.UnirestObjectMapper;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.pay.PayServerApplication;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.bean.enums.PaymentStatus;
import org.jfantasy.pay.product.sign.RSA;
import org.jfantasy.pay.service.PayConfigService;
import org.jfantasy.pay.service.PayProductConfiguration;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.pay.service.PaymentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PayServerApplication.class)
@Profile("dev")
public class AlipayTest {

    @Autowired
    private PayProductConfiguration payProductConfiguration;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PayConfigService payConfigService;
    @Autowired
    private PayService payService;
    private Alipay alipay;
    private PayConfig payConfig = new PayConfig();

    @Before
    public void setUp() throws Exception{
        alipay = payProductConfiguration.loadPayProduct("alipay");
    }
    //@Before
    public void setUp1() throws Exception {
//        payConfig.setName("正本上工支付宝支付");
//        payConfig.setPayConfigType(PayConfig.PayConfigType.online);
//        payConfig.setPayProductId("alipayDirect");
//        payConfig.setPayFeeType(PayConfig.PayFeeType.fixed);
//        payConfig.setPayFee(BigDecimal.ZERO);
//        payConfig.setBargainorId("2088021598024164");
//        payConfig.setBargainorKey("2s6pd34rf1u95t1hjlry13o1u13qxlbs");
//        payConfig.set("sellerEmail", "shzbsg@126.com");
        //一些高级接口需要使用 RSA 或者 DSA 加密
//        payConfig.set("rsaPrivateKey", FileUtil.readFile(new File("/certs/rsa_private_key.pem")));//RSA 加密时的私钥
//        payConfig.se／t("rsaPublicKey", FileUtil.readFile(new File("/certs/rsa_public_key.pem")));//RSA 支付宝公钥

        //查询配置
        payConfig.setName("正本上工支付宝支付");
        payConfig.setPayConfigType(PayConfig.PayConfigType.online);
        payConfig.setPayProductId("alipayDirect");
        payConfig.setPayFeeType(PayConfig.PayFeeType.fixed);
        payConfig.setPayFee(BigDecimal.ZERO);
        payConfig.setBargainorId("2016051201394880");
        payConfig.setBargainorKey("2s6pd34rf1u95t1hjlry13o1u13qxlbs");
        payConfig.set("sellerEmail", "2088021598024164");
        //一些高级接口需要使用 RSA 或者 DSA 加密
        payConfig.set("rsaPrivateKey","MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANIRPFVSTuGL4OGW" +
                "dbLTUDCEI4sVy80YfQh9dqnR+u57xWWrciC16jtdYQjxLFm9fOIq21D3u1UHcmhr" +
                "L4CAZrkjHL2Udfwusp827ogUyVyAJojccsEyFLk6KwoeYc3U9byBctGOwzLvWCCT" +
                "OMg8YK0JP9XaiMciSlunqhrOjDyNAgMBAAECgYA5EU2esDmVtHZnUoSvDBEg3QT6" +
                "5/TxxtFQ2SS/hbfxydYahLUAhesYLYoK79nolz2yA4qJOIO/2cIO8+93rWo6Kzeu" +
                "pnNsmAICB/GW/64jpKWLmcIxku909NbIALHk37tt4FPkcU6XuasHyzY753ll2/IZ" +
                "VK7KSDVjNG7PPkUbUQJBAPd/WcaSSUFusb2mWn9gk+csTQDpHigQV/P+uwgUZISM" +
                "dunAfIIoH0xtk9TddHbQT5cofV+YTSsCU+T/LFcQPTMCQQDZSLNf5AAQFQYBMhuB" +
                "b2hVquq2s79gvDMzQR4IwlExHl0tYhZl4hfUzLxkM4oZWETAmZlf9ofIsubkoVTF" +
                "TB8/AkEAghhWB3QDv7pBAbB853HLrPtzaqQfLu4QXXgrtf6KK8ZuB0cf64bNlO4Q" +
                "hBb4TjAHdixZYrN69L2ffcLH+ufVUwJBANEJ2lgkd9MBBtfbpw6tackRN+Ixp6qf" +
                "JProaMawe4Av4CCrPzUhgR/fIFeeJfwgKXTJ0P67pQJ26x+F/pIZm+0CQQDA5xPm" +
                "vjH2tlPB4H4uUEDWVQJnPLV/rhv+JWacZqMkp6gJMsHOiSmY8tMsHoiioMDujcDb" +
                "Zup+8ttxRYIwcAoO");//RSA 加密时的私钥
        payConfig.set("rsaPublicKey","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDI6d306Q8fIfCOaTXyiUeJHkrIvYISRcc73s3vF1ZT7XN8RNPwJxo8pWaJMmvyTn9N4HQ632qJBVHf8sxHi/fEsraprwCtzvzQETrNRwVxLO5jVmRGi60j8Ue1efIlzPXV9je9mkjzOmdssymZkh2QhUrCmZYI/FCEa3/cNMW0QIDAQAB");//RSA 支付宝公钥
    }

    @Test
    public void rsa() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
                "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMqwpkaWk4p8gjAh\n" +
                "rQ6Vgq6qW/+9HWaQZGW/NhFIu+GTaPA/q3uOd+l627OstDqFAZjEmPzM3tF6eodL\n" +
                "h7zJBANHYZXj8+9jGqLV1uGxpjlFucr51A1c2/1bx4rlOaCZ1pqcw8FFkY6PeucD\n" +
                "5veT47OrvsKmX/sHqsdB9942vtk/AgMBAAECgYEAqxUBtDEipdDEPoYeQWIXJQDs\n" +
                "mGby6wBTjcIgi+Q9mYBIIglL4AV31135FaZflclweJbwnuj55gygYZRyJPny5KKZ\n" +
                "ixica0Fpe0iSPKQ9KwIn3rx6gC/wdYlo3n8V6GR1+iBhWZ7yNOWB06SzJW26KXMv\n" +
                "Gtdi4ts43xSNkvn9W4kCQQD/D58SoSmBcNN19+hjgur7DFPFjzCiyyi5iLc/JnDQ\n" +
                "5+UL8bR4Cx5EjR9uxs1vN6ILZBoaPlz1ZVM4dv4aekj1AkEAy2+r/ldEauUTdHLg\n" +
                "Ac+xYwevvvOK9HUN4ZNypOSZrGxNZmBHRR6SR/axNE6RVN0zwC6cNQuXoP3TiAM1\n" +
                "LpaI4wJASkqxicqZfVNwtG7GKJ4MdZ1MlUG05+YG8aupvGIlACRbadQ4PbL3WP5G\n" +
                "Bo0vb1KkB29bzwMVLoEZ8VtvfiTaNQJAFaLIzgIF+sBmM0pMXKT0Hq4gmNRaAOm6\n" +
                "EjWWSccuONJD4RF4QvefYxvveLqqZjYoXNYYMuQKukqEhsCglVXZNQJBAInO/KxS\n" +
                "u1i3dcbwtH4NYjac7wBsCmuTQ1c8ETsDbhYEyeomRZaRzC+X782xCqAomz7ZYQZ3\n" +
                "O6gK7gMBQZ+rQtQ=\n" +
                "-----END PRIVATE KEY-----";

        privateKey = privateKey.replaceAll("^-----BEGIN PRIVATE KEY-----", "")
                .replaceAll("-----END PRIVATE KEY-----$", "")
                .replaceAll("\n", "");


        Assert.assertEquals(RSA.sign("a=123", privateKey, "utf-8"), "v6VeSlf6pQQhROZ0J+AQQv6zkJ+hWOhfuq3IZCkE0B4mEk33m7KqScEXdiM9gqnA2mxX1/MSDOKdMymsqXT8eyrA5Ny7FAgNku22WdziMLkx5dtT9BhXkmbe33QujVmxAMQAIOW1w0uqln/5a7WhJvTCzhgo8XFh2HT+qZ3MJVg=");
    }

    @Test
    public void sign() throws Exception {

    }

    @Test
    public void payNotifyByRefund() throws Exception {
        String result = "sign=cc4a046d5af1c621b1fcf8940d5cbbca&result_details=2016052621001004180237340740%5E0.10%5ESUCCESS&notify_time=2016-05-27+09%3A26%3A32&sign_type=MD5&notify_type=batch_refund_notify&notify_id=5176c054feac1371de0a3142edf2cf9j5x&batch_no=20160527RP201605260001101&success_num=1";
        Refund refund = new Refund();
        refund.setPayConfig(payConfig);
        alipay.payNotify(refund, result);
    }

    @Test
    public void close() throws Exception{
        payService.close("P2016052300002");
    }

    @Test
    @Transactional
    public void query() throws Exception{
        Payment payment = paymentService.get("P2016052300002");
        HashMap<String, String> query = alipay.query(payment);
        System.out.println(query);
    }
    @Test
    public void query1() throws Exception {
        Unirest.setObjectMapper(new UnirestObjectMapper(JSON.getObjectMapper()));

        Map<String, String> data = new HashMap<>();

        data.put("app_id", payConfig.getBargainorId());
        data.put("method", "alipay.trade.query");
        //data.put("format", "JSON");
        data.put("charset", "utf-8");
        data.put("sign_type", "RSA");
        data.put("timestamp",  DateUtil.format("yyyy-MM-dd HH:mm:ss"));
        data.put("version", "1.0");
        data.put("biz_content","{\"trade_no\":\"2017022521001004920266297785\"}");

//        Map<String,String> test = new HashMap<>();
//        test.put("a","123");
//        System.out.println(Alipay.sign(test,"RSA",Alipay.getPrivateKey(payConfig,"RSA")));

        data.put("sign", Alipay.sign(data, data.get("sign_type"),Alipay.getPrivateKey(payConfig, data.get("sign_type"))));

        HttpResponse<String> response = Unirest.post("https://openapi.alipay.com/gateway.do").queryString(new HashMap<>(data)).asString();

        System.out.println("response:" + response.getBody());
    }

//    public void xxx(){
//        AlipayClient alipayClient = new DefaultAlipayClient("","","","json","","","");
//    }


    @Test
    @Transactional
    public void payConfig(){
        //Payment payment = paymentService.get("PDEV2017022500013");
        PayConfig payConfig = payConfigService.get(6l);
        Properties properties = payConfig.getProperties();
        System.out.println(properties);
    }

    @Test
    public void updatePayConfig(){
        PayConfig payConfig = payConfigService.get(6l);
        payConfig.set("rsaPrivateKeyQ","MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANIRPFVSTuGL4OGW" +
                "dbLTUDCEI4sVy80YfQh9dqnR+u57xWWrciC16jtdYQjxLFm9fOIq21D3u1UHcmhr" +
                "L4CAZrkjHL2Udfwusp827ogUyVyAJojccsEyFLk6KwoeYc3U9byBctGOwzLvWCCT" +
                "OMg8YK0JP9XaiMciSlunqhrOjDyNAgMBAAECgYA5EU2esDmVtHZnUoSvDBEg3QT6" +
                "5/TxxtFQ2SS/hbfxydYahLUAhesYLYoK79nolz2yA4qJOIO/2cIO8+93rWo6Kzeu" +
                "pnNsmAICB/GW/64jpKWLmcIxku909NbIALHk37tt4FPkcU6XuasHyzY753ll2/IZ" +
                "VK7KSDVjNG7PPkUbUQJBAPd/WcaSSUFusb2mWn9gk+csTQDpHigQV/P+uwgUZISM" +
                "dunAfIIoH0xtk9TddHbQT5cofV+YTSsCU+T/LFcQPTMCQQDZSLNf5AAQFQYBMhuB" +
                "b2hVquq2s79gvDMzQR4IwlExHl0tYhZl4hfUzLxkM4oZWETAmZlf9ofIsubkoVTF" +
                "TB8/AkEAghhWB3QDv7pBAbB853HLrPtzaqQfLu4QXXgrtf6KK8ZuB0cf64bNlO4Q" +
                "hBb4TjAHdixZYrN69L2ffcLH+ufVUwJBANEJ2lgkd9MBBtfbpw6tackRN+Ixp6qf" +
                "JProaMawe4Av4CCrPzUhgR/fIFeeJfwgKXTJ0P67pQJ26x+F/pIZm+0CQQDA5xPm" +
                "vjH2tlPB4H4uUEDWVQJnPLV/rhv+JWacZqMkp6gJMsHOiSmY8tMsHoiioMDujcDb" +
                "Zup+8ttxRYIwcAoO");
        payConfig.set("rsaPrivateKey","MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBANIyubyDZB728Na2" +
                "wDfqUJEXq3C/DBmWPX/1FiEI6GuCu0lb4ulCsCF/qASWGrnIijZ0ZfjXpB79gihZ" +
                "2FgP/g86IRPf7lLNT0L+X6ux+b4ENNANwXtYFcW+lhOpylbLUC1t2uaOjpoZ2Srd" +
                "eoVlb9NNCkpk9uK9bwDn2AD/Ns2dAgMBAAECgYAGsmcIgocmFWgG7zugjG6UsNRd" +
                "ezi/d/HtqblSxB3jjv64j5zjIaTK7G5F9yJS2PjOU1cMXpJ0Ck+jSXmDFL9bXgap" +
                "ggtbydfHj4RhJx1FU6YXRC4+n9cqnMh6Cwx5y3i6bsYeAIYqw7NigM39ifLmgmGL" +
                "da/UjOaYfpdBezekYQJBAPebjzCJW5+fgMxgwmM5wMuiTDPdxwJcgF/syLr4xbYc" +
                "0NjZIzgl9tgh8ld9N4pmZAOsT+zSUb5NDEudKY2KIisCQQDZUpJUj2hRilCA9gBr" +
                "Yz7TIGE+PZkbJNzqiwfNpsvwmaBSPf1ftL2CraHw10EcsxB6ZxIajzQ3osPpvz8c" +
                "NBNXAkAu3S54zUaeK55BEH86MJAg+pLZrjwgYkmZ3kMPwE4LbeDJai+UTPsvZR1t" +
                "GbINa9u6Jj7qX9RA5GxTU2et9lsJAkEArpKC04SDcwTdmEqEmb8Wh3h6RQosRD6/" +
                "a3UVZqC3MGXoAEilkUzZ8vBRpury9f/tm7XSOB2S/6IzKECljJ1UbwJAHbouY5Jz" +
                "7xvZUHovYtsTI47pbwNaozBDQk4YOZgu68sJ7SIs9dYhS+3/cxSiHBglk69UL/7X" +
                "oc/kb8dK+jrAPw==");
        payConfigService.update(payConfig);
        //payConfigService.save(payConfig);
    }
}