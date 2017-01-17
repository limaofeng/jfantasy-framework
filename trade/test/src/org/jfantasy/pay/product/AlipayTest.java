package org.jfantasy.pay.product;

import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.product.sign.RSA;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;


public class AlipayTest {

    private Alipay alipay = new Alipay();
    private PayConfig payConfig = new PayConfig();

    @Before
    public void setUp() throws Exception {
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
        payConfig.set("rsaPrivateKey", "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMOvpizdjcG0rn/8niR1GfWT3XX4iu1czbQLeORd2mzMMKY7Qjd24ybVJgVjoGSYc5sjtFFESwjJnbLZ9fKZPsonXvMj94Q1CPghjpbdfLhAsHlT5X7Skx26sda7UXq3jgmuIlhKYMb7GAlZX+2usX5lnLlyN+WcYR+68FgIhIoXAgMBAAECgYBpk/hFVpfn/fL0LLiqFOAXplqjDqDuJdb6IAJuu9BgSN6qoWg9gpBV4ERuPe1IuRQOjPn5qq4NJLJHz98pr9K2zX+45BFjL78FRLEs1jy5o4UqKgMZriyErvul2zWVWPAlbOzDx/Bike5e/oPT9Nll7rC7dAN8oRQpWk2Zdg+2yQJBAP5OJ/dtbbSffVsAXq5pb4gdd4Nu8HiUWK4v7eJsUMuqkFJwpJBTOigr3MVF0XivlTZaDXMhsyIotQPV7PVkb0MCQQDE/X0qC/yPRF377tsAdcGryi+DFkmmIxgNVnEOCn8cbA/MUY6CCSFHP5n54i9R2Zt0f8xgKyq4IZ9EaDbKKpqdAkByVX9AjhFpyN8aP/NRpRPA9caa8BDrlX69ac0hJKO6vce/WOeT/+dA0l+izf4crYx/cENlxPv92qFvxQmRVoNzAkBStdBg4CmKCf2gMzj253qK49ixJKGFxURrDTlo2NVoHKNBeZjpVmYHoVfISMTvi/uunZ41XsqQB2X09gDTP7ItAkAoZuS6xBY5j74Auf3GC5MSZLsAeW1ytoyAurKPYpC7qhE1fENyYYgh5ZOdvziky2RGlqlFsnnbtbXlaVhMiW4P");//RSA 加密时的私钥
        payConfig.set("rsaPublicKey", "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDr6Ys3Y3BtK5//J4kdRn1k911+IrtXM20C3jkXdpszDCmO0I3duMm1SYFY6BkmHObI7RRREsIyZ2y2fXymT7KJ17zI/eENQj4IY6W3Xy4QLB5U+V+0pMdurHWu1F6t44JriJYSmDG+xgJWV/trrF+ZZy5cjflnGEfuvBYCISKFwIDAQAB");//RSA 支付宝公钥

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

        privateKey = privateKey.replaceAll("^-----BEGIN PRIVATE KEY-----","")
                .replaceAll("-----END PRIVATE KEY-----$","")
                .replaceAll("\n","");


        Assert.assertEquals(RSA.sign("a=123", privateKey, "utf-8"),"v6VeSlf6pQQhROZ0J+AQQv6zkJ+hWOhfuq3IZCkE0B4mEk33m7KqScEXdiM9gqnA2mxX1/MSDOKdMymsqXT8eyrA5Ny7FAgNku22WdziMLkx5dtT9BhXkmbe33QujVmxAMQAIOW1w0uqln/5a7WhJvTCzhgo8XFh2HT+qZ3MJVg=");
    }

    @Test
    public void sign() throws Exception {

    }

    @Test
    public void payNotifyByRefund() throws Exception {
        String result = "sign=cc4a046d5af1c621b1fcf8940d5cbbca&result_details=2016052621001004180237340740%5E0.10%5ESUCCESS&notify_time=2016-05-27+09%3A26%3A32&sign_type=MD5&notify_type=batch_refund_notify&notify_id=5176c054feac1371de0a3142edf2cf9j5x&batch_no=20160527RP201605260001101&success_num=1";
        Refund refund = new Refund();
        refund.setPayConfig(payConfig);
        alipay.payNotify(refund,result);
    }

    @Test
    public void query() throws Exception {
    }

}