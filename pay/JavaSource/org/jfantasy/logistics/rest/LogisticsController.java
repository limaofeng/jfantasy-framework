package org.jfantasy.logistics.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.logistics.bean.DeliveryItem;
import org.jfantasy.logistics.bean.DeliveryType;
import org.jfantasy.logistics.bean.Express;
import org.jfantasy.logistics.bean.Logistics;
import org.jfantasy.logistics.rest.form.LogisticsForm;
import org.jfantasy.logistics.service.LogisticsService;
import org.jfantasy.order.bean.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 配送的送货信息
 */
@RestController
@RequestMapping("/logisticss")
public class LogisticsController {

    private LogisticsService logisticsService;

    /** 按条件检索送货信息 - 筛选送货信息，返回通用分页对象 **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Logistics> search(@ApiParam(value = "分页对象", name = "pager") Pager<Logistics> pager, @ApiParam(value = "过滤条件", name = "filters") List<PropertyFilter> filters) {
        return this.logisticsService.findPager(pager, filters);
    }

    @ApiOperation(value = "获取送货信息", notes = "通过该接口, 获取单篇送货信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Logistics view(@PathVariable("id") String id) {
        return this.logisticsService.get(id);
    }

    /** 送货信息的详细物流项 - 送货信息的详细物流项 **/
    @RequestMapping(value = "/{id}/items", method = RequestMethod.GET)
    @ResponseBody
    public List<DeliveryItem> deliveryItems(@PathVariable("id") String id) {
        return this.logisticsService.get(id).getDeliveryItems();
    }

    /** 送货信息对应的订单信息 - 送货信息对应的订单信息 **/
    @RequestMapping(value = "/{id}/order", method = RequestMethod.GET)
    @ResponseBody
    public Order order(@PathVariable("id") String id) {
        return this.logisticsService.getOrder(id);
    }

    /** 送货信息的配送方式 - 送货信息的配送方式 **/
    @RequestMapping(value = "/{id}/type", method = RequestMethod.GET)
    @ResponseBody
    public DeliveryType deliveryType(@PathVariable("id") String id) {
        return this.logisticsService.get(id).getDeliveryType();
    }

    /**
     * 送货信息的配送物流公司 - 送货信息的配送物流公司
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/express", method = RequestMethod.GET)
    @ResponseBody
    public Express express(@PathVariable("id") String id) {
        return this.logisticsService.get(id).getDeliveryType().getExpress();
    }

    @ApiOperation(value = "添加送货信息", notes = "通过该接口, 添加送货信息")
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Logistics create(@RequestBody LogisticsForm form) {
        return this.logisticsService.save(form.getDeliveryTypeId(), form.getOrderId(), form.getDeliveryItems());
    }

    @ApiOperation(value = "删除送货信息", notes = "通过该接口, 删除送货信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public void delete(@PathVariable("id") String id) {
        this.logisticsService.delete(id);
    }

    @Autowired
    public void setLogisticsService(LogisticsService logisticsService) {
        this.logisticsService = logisticsService;
    }

}
