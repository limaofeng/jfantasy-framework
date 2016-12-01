package org.jfantasy.pay.rest;

import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 支付操作
 **/
@RestController
@RequestMapping("/pays")
public class PayController {

    private final PayService payService;

    @Autowired
    public PayController(PayService payService) {
        this.payService = payService;
    }

    /**
     * 支付通知 - 用于第三方支付通知系统
     *
     * @param sn   交易单号
     * @param body 通知内容
     * @return Object
     */
    @JsonResultFilter(ignore = {
            @IgnoreProperty(pojo = Order.class, name = {"pay_config"}),
            @IgnoreProperty(pojo = Payment.class, name = {"order", "pay_config"}),
            @IgnoreProperty(pojo = Refund.class, name = {"order", "payment", "pay_config"})
    })
    @RequestMapping(value = "/{sn}/notify", method = RequestMethod.POST)
    @ResponseBody
    public Object notify(@PathVariable("sn") String sn, @RequestBody String body) throws PayException {
        if (sn.startsWith("RP")) {
            return payService.refundNotify(sn, body);
        } else if (sn.startsWith("P")) {
            return payService.paymentNotify(sn, body);
        } else {
            throw new PayException("不能处理的订单");
        }
    }

    @RequestMapping(value = "/{sn}/query", method = RequestMethod.GET)
    public boolean query(@PathVariable("sn") String sn) throws PayException {
        return payService.query(sn);
    }

}
