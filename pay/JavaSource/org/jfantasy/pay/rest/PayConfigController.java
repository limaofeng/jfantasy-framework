package org.jfantasy.pay.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.pay.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.product.Parameters;
import org.jfantasy.pay.product.PayProduct;
import org.jfantasy.pay.rest.models.assembler.PayConfigResourceAssembler;
import org.jfantasy.pay.service.PayConfigService;
import org.jfantasy.pay.service.PayProductConfiguration;
import org.jfantasy.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

/** 支付配置 **/
@RestController
@RequestMapping("/payconfigs")
public class PayConfigController {

    private PayConfigResourceAssembler assembler = new PayConfigResourceAssembler();

    @Autowired
    private PayConfigService configService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PayProductConfiguration payProductConfiguration;
    @Autowired
    private PaymentController paymentController;
    @Autowired
    private RefundController refundController;

    @JsonResultFilter(ignore = @IgnoreProperty(pojo = PayConfig.class, name = {"properties"}))
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<PayConfig> pager, List<PropertyFilter> filters) {
        return assembler.toResources(this.configService.findPager(pager, filters));
    }

    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PayConfig create(@RequestBody PayConfig config) {
        return this.configService.save(config);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public PayConfig update(@PathVariable("id") Long id, @RequestBody PayConfig config) {
        config.setId(id);
        return this.configService.update(config);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") Long id) {
        return assembler.toResource(this.configService.get(id));
    }

    @RequestMapping(value = "/{id}/payproduct", method = RequestMethod.POST)
    @ResponseBody
    public PayProduct payproduct(@PathVariable("id") Long id) throws IOException, PayException {
        return this.payProductConfiguration.loadPayProduct(this.configService.get(id).getPayProductId());
    }

    @RequestMapping(value = "/{id}/test", method = RequestMethod.POST)
    @ResponseBody
    public String test(@PathVariable("id") Long paymentConfigId, @RequestBody Parameters parameters) throws IOException, PayException {
        return this.paymentService.test(paymentConfigId, parameters);
    }

    /** 删除支付配置 **/
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.configService.delete(id);
    }

    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Payment.class, name = {"payConfig", "orderKey"}),
            allow = @AllowProperty(pojo = Order.class, name = {"type", "subject", "sn"})
    )
    /** 支付配置对应的支付记录 **/
    @RequestMapping(value = "/{id}/payments", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> payments(@PathVariable("id") String id, Pager<Payment> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQL_payConfig.id", id));
        return paymentController.search(pager, filters);
    }

    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Refund.class, name = {"payConfig", "orderKey"}),
            allow = {
                    @AllowProperty(pojo = Order.class, name = {"type", "subject", "sn"}),
                    @AllowProperty(pojo = Payment.class, name = {"totalAmount", "payConfigName", "sn", "status"})
            }
    )
    /** 支付配置对应的退款记录 **/
    @RequestMapping(value = "/{id}/refunds", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> refunds(@PathVariable("id") String id, Pager<Refund> pager, List<PropertyFilter> filters) {
        filters.add(new PropertyFilter("EQL_payConfig.id", id));
        return refundController.search(pager, filters);
    }

}