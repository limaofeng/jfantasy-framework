package org.jfantasy.pay.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.rest.models.assembler.PaymentResourceAssembler;
import org.jfantasy.pay.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 支付记录
 **/
@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentResourceAssembler assembler = new PaymentResourceAssembler();

    private PaymentService paymentService;
    private PayConfigController payConfigController;

    /**
     * 查询支付记录
     *
     * @param pager   分页
     * @param filters 筛选
     * @return Pager
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Payment.class, name = {"pay_config", "order_id"}),
            allow = @AllowProperty(pojo = Order.class, name = {"type", "id"})
    )
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Payment> pager, List<PropertyFilter> filters) {
        return assembler.toResources(paymentService.findPager(pager, filters));
    }

    /**
     * 获取支付记录
     **/
    @RequestMapping(value = "/{sn}", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("sn") String sn) {
        return assembler.toResource(this.paymentService.get(sn));
    }

    /**
     * 支付记录对应的支付配置信息
     **/
    @RequestMapping(value = "/{sn}/payconfig", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport payconfig(@PathVariable("sn") String sn) {
        return payConfigController.view(get(sn).getPayConfig().getId());
    }

    /**
     * 支付记录对应的订单信息 - 支付记录对应的订单信息
     *
     * @param sn 编码
     * @return ResultResource
     */
    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = Order.class, name = {"refunds", "orderItems", "payments"}),
            allow = {@AllowProperty(pojo = PayConfig.class, name = {"id", "name"}),
                    @AllowProperty(pojo = Payment.class, name = {"id", "name"})}
    )
    @RequestMapping(value = "/{sn}/order", method = RequestMethod.GET)
    public ModelAndView order(@PathVariable("sn") String sn) {
        return new ModelAndView("redirect:/orders/" + get(sn).getOrderId());
    }

    /**
     * 删除支付记录
     **/
    @RequestMapping(value = "/{sn}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("sn") String sn) {
        this.paymentService.delete(sn);
    }

    /**
     * 批量删除支付记录
     **/
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@RequestBody String... sns) {
        this.paymentService.delete(sns);
    }

    private Payment get(String sn) {
        Payment payment = this.paymentService.get(sn);
        if (payment == null) {
            throw new NotFoundException("[sn=" + sn + "]对应的支付记录未找到");
        }
        return payment;
    }

    @Autowired
    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Autowired
    public void setPayConfigController(PayConfigController payConfigController) {
        this.payConfigController = payConfigController;
    }

}
