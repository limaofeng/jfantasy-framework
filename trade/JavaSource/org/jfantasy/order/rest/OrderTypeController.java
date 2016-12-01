package org.jfantasy.order.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.order.bean.OrderType;
import org.jfantasy.order.service.OrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
@RequestMapping("/order-types")
public class OrderTypeController {

    private OrderTypeService orderTypeService;

    @GetMapping
    @ResponseBody
    public Pager<OrderType> search(Pager<OrderType> pager, List<PropertyFilter> filters) {
        return orderTypeService.findPager(pager, filters);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public OrderType create(@Validated(RESTful.POST.class) @RequestBody OrderType orderType) {
        return orderTypeService.save(orderType);
    }

    @PutMapping("/{id}")
    @ResponseBody
    public OrderType update(HttpServletRequest request, @PathVariable("id") String id, @RequestBody OrderType orderType) {
        orderType.setId(id);
        return orderTypeService.update(orderType, WebUtil.has(request, RequestMethod.PATCH));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.orderTypeService.delete(id);
    }

    @Autowired
    public void setOrderTypeService(OrderTypeService orderTypeService) {
        this.orderTypeService = orderTypeService;
    }

}
