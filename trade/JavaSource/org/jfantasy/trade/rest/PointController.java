package org.jfantasy.trade.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.trade.bean.Account;
import org.jfantasy.trade.bean.Point;
import org.jfantasy.pay.rest.models.PointForm;
import org.jfantasy.pay.rest.models.assembler.PointResourceAssembler;
import org.jfantasy.trade.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 积分
 **/
@RestController
@RequestMapping("/points")
public class PointController {

    private PointResourceAssembler assembler = new PointResourceAssembler();

    private final PointService pointService;

    @Autowired
    public PointController(PointService pointService) {
        this.pointService = pointService;
    }

    @JsonResultFilter(allow = @AllowProperty(pojo = Account.class, name = {"sn", "type"}))
    /** 积分查询 **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters",name = "filters",paramType = "query",dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<Point> pager, List<PropertyFilter> filters) {
        return assembler.toResources(pointService.findPager(pager, filters));
    }

    @JsonResultFilter(allow = @AllowProperty(pojo = Account.class, name = {"sn", "type"}))
    /** 提交积分记录 - 消费与新增积分 **/
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport save(Point point) {
        return assembler.toResource(pointService.save(point));
    }

    /**
     * 更新积分记录<br/>
     * 该方法只能修改 积分状态
     *
     * @param id
     * @param form
     * @return
     */
    @JsonResultFilter(allow = @AllowProperty(pojo = Account.class, name = {"sn", "type"}))
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public ResultResourceSupport update(@PathVariable("id") String id, @RequestBody PointForm form) {
        return assembler.toResource(pointService.update(id, form.getStatus(), form.getRemark()));
    }

}