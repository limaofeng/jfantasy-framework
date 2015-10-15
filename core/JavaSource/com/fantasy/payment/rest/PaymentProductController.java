package com.fantasy.payment.rest;


import com.fantasy.payment.product.PaymentProduct;
import com.fantasy.payment.service.PaymentConfiguration;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(value = "system-payproducts", description = "支付产品")
@RestController
@RequestMapping("/system/payproducts")
public class PaymentProductController {

    @Autowired
    private PaymentConfiguration paymentConfiguration;

    @ApiOperation(value = "获取支付产品", notes = "通过该接口, 可以获取支持的支付产品列表")
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<PaymentProduct> search() {
        return paymentConfiguration.getPaymentProducts();
    }

}