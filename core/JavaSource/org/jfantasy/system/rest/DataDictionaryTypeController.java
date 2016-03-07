package org.jfantasy.system.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.system.bean.Dict;
import org.jfantasy.system.bean.DictType;
import org.jfantasy.system.service.DataDictionaryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "system-ddts", description = "数据字典分类")
@RestController
@RequestMapping("/system/ddts")
public class DataDictionaryTypeController {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @ApiOperation(value = "添加数据字典分类", notes = "通过该接口, 可以添加新的数据字典分类。", response = DictType.class)
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public DictType create(@RequestBody DictType ddt) {
        return this.dataDictionaryService.save(ddt);
    }

    @RequestMapping(method = RequestMethod.PUT)
    @ResponseBody
    public DictType update(@RequestBody DictType ddt) {
        return this.dataDictionaryService.save(ddt);
    }

    @ApiOperation(value = "获取数据字典分类", notes = "通过该接口, 可以获取 code 对应的字典分类信息。", response = DictType.class)
    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public DictType view(@PathVariable("code") String code) {
        return this.dataDictionaryService.getDataDictionaryType(code);
    }

    @ApiOperation(value = "查询数据字典分类", notes = "通过该接口, 可以筛选需要的数据字典分类信息。", response = Pager.class)
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<DictType> search(Pager<DictType> pager, List<PropertyFilter> filters) {
        return this.dataDictionaryService.findDataDictionaryTypePager(pager, filters);
    }

    @ApiOperation(value = "删除数据字典分类", notes = "通过该接口删除数据字典分类")
    @RequestMapping(value = "/{code}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("code") String code) {
        this.dataDictionaryService.deleteType(code);
    }

    @ApiOperation(value = "获取数据字典分类下的字典项", notes = "通过该接口, 可以获取 code 对应的所有字典信息。", response = DictType.class)
    @RequestMapping(value = "/{code}/dds", method = RequestMethod.GET)
    @ResponseBody
    public List<Dict> dds(@PathVariable("code") String code) {
        return this.dataDictionaryService.find(Restrictions.eq("type", code));
    }

    @ApiOperation(value = "在数据字典分类下添加数据字典项", notes = "通过该接口, 可以添加新的数据项。", response = Dict.class)
    @RequestMapping(value = "/{code}/dds", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Dict dds(@PathVariable("code") String code, @RequestBody Dict dd) {
        dd.setType(code);
        return this.dataDictionaryService.save(dd);
    }
}
