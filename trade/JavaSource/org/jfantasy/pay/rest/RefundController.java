package org.jfantasy.pay.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.order.bean.Order;
import org.jfantasy.pay.bean.PayConfig;
import org.jfantasy.pay.bean.Payment;
import org.jfantasy.pay.bean.Refund;
import org.jfantasy.pay.error.PayException;
import org.jfantasy.pay.rest.models.RefundForm01;
import org.jfantasy.pay.rest.models.assembler.RefundResourceAssembler;
import org.jfantasy.pay.service.PayService;
import org.jfantasy.pay.service.RefundService;
import org.jfantasy.pay.service.vo.ToRefund;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 退款记录
 **/
@RestController
@RequestMapping("/refunds")
public class RefundController {

    private static final Log LOG = LogFactory.getLog(RefundController.class);

    private RefundResourceAssembler assembler = new RefundResourceAssembler();

    private final RefundService refundService;
    private final PayService payService;

    @Autowired
    public RefundController(PayService payService, RefundService refundService) {
        this.payService = payService;
        this.refundService = refundService;
    }

    /**
     * 查询退款记录
     *
     * @param pager
     * @param filters
     * @return
     */
    @JsonResultFilter(allow = {
            @AllowProperty(pojo = Order.class, name = {"key", "status", "type", "sn"}),
            @AllowProperty(pojo = Payment.class, name = {"sn", "total_amount", "status"}),
            @AllowProperty(pojo = PayConfig.class, name = {"id", "x", "name"})
    })
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Refund> pager, List<PropertyFilter> filters) {
        if (!pager.isOrderBySetted()) {
            pager.sort(Refund.FIELDS_BY_CREATE_TIME, Pager.SORT_DESC);
        }
        return assembler.toResources(refundService.findPager(pager, filters));
    }

    /**
     * 更新退款记录<br/>
     * 该方法只能修改 退款状态
     *
     * @param sn
     * @param form
     * @return
     */
    @JsonResultFilter(allow = {
            @AllowProperty(pojo = Order.class, name = {"key", "status", "type", "sn"}),
            @AllowProperty(pojo = Payment.class, name = {"sn", "total_amount", "status"}),
            @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name"})
    })
    @RequestMapping(value = "/{sn}/status", method = RequestMethod.PUT)
    @ResponseBody
    public ToRefund update(@PathVariable("sn") String sn, @RequestBody RefundForm01 form) {
        try {
            return payService.refund(sn, form.getStatus(), form.getRemark());
        } catch (PayException e) {
            LOG.error(e);
            throw new RestException(e.getMessage());
        }
    }

    /**
     * 获取退款记录
     *
     * @param sn
     * @return
     */
    @JsonResultFilter(allow = {
            @AllowProperty(pojo = Order.class, name = {"key", "status", "type", "sn"}),
            @AllowProperty(pojo = Payment.class, name = {"sn", "total_amount", "status"}),
            @AllowProperty(pojo = PayConfig.class, name = {"id", "pay_product_id", "name"})
    })
    @RequestMapping(value = "/{sn}", method = RequestMethod.GET)
    @ResponseBody
    public ResultResourceSupport view(@PathVariable("sn") String sn) {
        return assembler.toResource(this.refundService.get(sn));
    }

    /**
     * 删除退款记录
     **/
    @RequestMapping(value = "/{sn}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("sn") String sn) {
        this.refundService.delete(sn);
    }

    /**
     * 批量删除退款记录
     **/
    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void delete(@RequestBody String... sns) {
        this.refundService.delete(sns);
    }

    @RequestMapping(value = "/{sn}/order", method = RequestMethod.GET)
    public ModelAndView order(@PathVariable("sn") String sn) {
        Refund refund = this.refundService.get(sn);
        if (refund == null) {
            throw new NotFoundException("对象不存在");
        }
        return new ModelAndView("redirect:/orders/" + refund.getOrderId());
    }

    @RequestMapping(value = "/{sn}/payconfig", method = RequestMethod.GET)
    public ModelAndView payconfig(@PathVariable("sn") String sn) {
        Refund refund = this.refundService.get(sn);
        if (refund == null) {
            throw new NotFoundException("对象不存在");
        }
        return new ModelAndView("redirect:/payconfigs/" + refund.getPayConfig().getId());
    }

    @RequestMapping(value = "/{sn}/payment", method = RequestMethod.GET)
    public ModelAndView payment(@PathVariable("sn") String sn) {
        Refund refund = this.refundService.get(sn);
        if (refund == null) {
            throw new NotFoundException("对象不存在");
        }
        return new ModelAndView("redirect:/payments/" + refund.getSn());
    }

}
