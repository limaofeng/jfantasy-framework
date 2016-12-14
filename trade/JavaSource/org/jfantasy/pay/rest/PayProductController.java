package org.jfantasy.pay.rest;


import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.product.PayProductSupport;
import org.jfantasy.pay.rest.models.assembler.PayProductResourceAssembler;
import org.jfantasy.pay.service.PayProductConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * 支付产品
 **/
@RestController
@RequestMapping("/payproducts")
public class PayProductController {

    private PayProductResourceAssembler assembler = new PayProductResourceAssembler();

    private final PayProductConfiguration payProductConfiguration;

    @Autowired
    public PayProductController(PayProductConfiguration payProductConfiguration) {
        this.payProductConfiguration = payProductConfiguration;
    }

    /**
     * 获取支付产品<br/>
     *
     * @return 通过该接口, 可以获取支持的支付产品列表
     */
    @JsonResultFilter(allow = {@AllowProperty(pojo = PayProductSupport.class, name = {"id", "name"})})
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<ResultResourceSupport> search() {
        return assembler.toResources(payProductConfiguration.getPayProducts());
    }

    /**
     * 支付产品详情<br/>
     * 通过该接口, 可以获取支付产品详情
     *
     * @param id id
     * @return Product
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("id") String id) {
        return assembler.toResource(payProductConfiguration.loadPayProduct(id));
    }

    /**
     * 适用于该支付产品的支付配置 - 查看产品的支付配置信息
     *
     * @param id      id
     * @param pager   分页
     * @param filters 筛选
     * @return Pager
     */
    @RequestMapping(value = "/{id}/payconfigs", method = RequestMethod.GET)
    @ResponseBody
    public ModelAndView payconfigs(@PathVariable("id") String id, RedirectAttributes attrs, Pager<PayConfig> pager, List<PropertyFilter> filters) {
        attrs.addAttribute("EQS_payProductId", id);
        pager.writeTo(attrs).write(filters);
        return new ModelAndView("redirect:/payconfigs");
    }

}
