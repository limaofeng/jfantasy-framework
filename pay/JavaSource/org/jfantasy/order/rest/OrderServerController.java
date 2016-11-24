package org.jfantasy.order.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.order.bean.OrderServer;
import org.jfantasy.pay.rest.models.OrderServerForm;
import org.jfantasy.order.service.OrderServerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 订单服务 **/
@RestController
@RequestMapping("/order-server")
public class OrderServerController {

    private final OrderServerService orderServerService;

    @Autowired
    public OrderServerController(OrderServerService orderServerService) {
        this.orderServerService = orderServerService;
    }

    /** 查询订单服务 **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<OrderServer> search(Pager<OrderServer> pager, List<PropertyFilter> filters) {
        return orderServerService.findPager(pager, filters);
    }

    /** 保存订单服务 **/
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public OrderServer create(@RequestBody OrderServerForm form){
        return orderServerService.save(form.getCallType(),form.getUrl(),form.getDescription(),form.getProperties());
    }

}
