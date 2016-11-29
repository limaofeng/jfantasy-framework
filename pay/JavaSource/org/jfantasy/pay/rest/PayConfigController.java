package org.jfantasy.pay.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.product.Parameters;
import org.jfantasy.pay.rest.models.assembler.PayConfigResourceAssembler;
import org.jfantasy.pay.service.PayConfigService;
import org.jfantasy.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

/**
 * 支付配置
 **/
@RestController
@RequestMapping("/payconfigs")
public class PayConfigController {

    private PayConfigResourceAssembler assembler = new PayConfigResourceAssembler();

    @Autowired
    private PayConfigService configService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundController refundController;

    @JsonResultFilter(ignore = @IgnoreProperty(pojo = PayConfig.class, name = {"properties"}))
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
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
    public ModelAndView payproduct(@PathVariable("id") Long id) throws IOException, PayException {
        return new ModelAndView("redirect:/payproducts/" + this.configService.get(id).getPayProductId());
    }

    @RequestMapping(value = "/{id}/test", method = RequestMethod.POST)
    @ResponseBody
    public String test(@PathVariable("id") Long paymentConfigId, @RequestBody Parameters parameters) throws IOException, PayException {
        return this.paymentService.test(paymentConfigId, parameters);
    }

    /**
     * 删除支付配置
     **/
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.configService.delete(id);
    }

    /**
     * 支付配置对应的支付记录
     *
     * @param id
     * @param pager
     * @param filters
     * @return
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Payment.class, name = {"payConfig", "orderKey"}),
            allow = @AllowProperty(pojo = Order.class, name = {"type", "subject", "sn"})
    )
    @RequestMapping(value = "/{id}/payments", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public ModelAndView payments(@PathVariable("id") String id, RedirectAttributes attrs, Pager<Payment> pager, List<PropertyFilter> filters) {
        attrs.addAttribute("EQL_payConfig.id", id);
        attrs.addAttribute("page", pager.getCurrentPage());
        attrs.addAttribute("per_page", pager.getPageSize());
        if (pager.isOrderBySetted()) {
            attrs.addAttribute("sort", pager.getOrderBy());
            attrs.addAttribute("order", pager.getOrder());
        }
        for (PropertyFilter filter : filters) {
            attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
        }
        return new ModelAndView("redirect:/payments");
    }

    /**
     * 支付配置对应的退款记录
     *
     * @param id      支付ID
     * @param pager   翻页对象
     * @param filters 筛选条件
     * @return ModelAndView
     */
    @RequestMapping(value = "/{id}/refunds", method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public ModelAndView refunds(@PathVariable("id") String id, RedirectAttributes attrs, Pager<Refund> pager, List<PropertyFilter> filters) {
        attrs.addAttribute("EQL_payConfig.id", id);
        attrs.addAttribute("page", pager.getCurrentPage());
        attrs.addAttribute("per_page", pager.getPageSize());
        if (pager.isOrderBySetted()) {
            attrs.addAttribute("sort", pager.getOrderBy());
            attrs.addAttribute("order", pager.getOrder());
        }
        for (PropertyFilter filter : filters) {
            attrs.addAttribute(filter.getFilterName(), filter.getPropertyValue());
        }
        return new ModelAndView("redirect:/refunds");
    }

}