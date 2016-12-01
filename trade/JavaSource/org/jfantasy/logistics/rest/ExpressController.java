package org.jfantasy.logistics.rest;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jfantasy.logistics.bean.Express;
import org.jfantasy.logistics.service.ExpressService;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 快递公司
 */
@RestController
@RequestMapping("/expresses")
public class ExpressController {

    @Autowired
    private ExpressService expressService;

    @ApiOperation(value = "按条件检索物流公司", notes = "筛选物流公司，返回通用分页对象", response = Express[].class)
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Express> search(Pager<Express> pager, @ApiParam(value = "过滤条件", name = "filters") List<PropertyFilter> filters) {
        return this.expressService.findPager(pager, filters);
    }

    @ApiOperation(value = "获取物流公司", notes = "通过该接口, 获取单篇物流公司", response = Express.class)
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Express view(@PathVariable("id") String id) {
        return this.expressService.get(id);
    }

    @ApiOperation(value = "添加物流公司", notes = "通过该接口, 添加物流公司", response = Express.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Express create(@RequestBody Express corp) {
        return this.expressService.save(corp);
    }

    @ApiOperation(value = "更新物流公司", notes = "通过该接口, 更新物流公司")
    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    public Express update(@PathVariable("id") String id, @RequestBody Express express) {
        express.setId(id);
        return this.expressService.save(express);
    }

    /**
     * 删除物流公司<br/>
     * 通过该接口, 删除物流公司
     *
     * @param id ID
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.expressService.delete(id);
    }

}
