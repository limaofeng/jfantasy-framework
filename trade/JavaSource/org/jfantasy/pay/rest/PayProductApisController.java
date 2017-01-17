package org.jfantasy.pay.rest;

import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.product.PayProduct;
import org.jfantasy.pay.service.PayProductConfiguration;
import org.jfantasy.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payproducts/{id}/apis")
public class PayProductApisController {

    private final PaymentService paymentService;
    private final PayProductConfiguration payProductConfiguration;

    @Autowired
    public PayProductApisController(PaymentService paymentService, PayProductConfiguration payProductConfiguration) {
        this.paymentService = paymentService;
        this.payProductConfiguration = payProductConfiguration;
    }

    /*
    @GetMapping("/web")
    @ResponseBody
    public Object web(@PathVariable("id") String id) {
        PayProduct payProduct = payProductConfiguration.loadPayProduct(id);
        return payProduct.web(null,null,null);
    }

    @GetMapping("/app")
    @ResponseBody
    public Object app(@PathVariable("id") String id) {
        PayProduct payProduct = payProductConfiguration.loadPayProduct(id);
        return payProduct.app(null,null,null);
    }*/

    @GetMapping("/query")
    @ResponseBody
    public Object query(@PathVariable("id") String id, @RequestParam("pid") String pid) throws PayException {
        PayProduct payProduct = payProductConfiguration.loadPayProduct(id);
        Payment payment = this.paymentService.get(pid);
        return payProduct.query(payment);
    }

}
