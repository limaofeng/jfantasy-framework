package org.jfantasy.system.rest;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.system.bean.DataDictionary;
import org.jfantasy.system.bean.DataDictionaryType;
import org.jfantasy.system.service.DataDictionaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** 数据字典分类 **/
@RestController
@RequestMapping("/system/ddts")
public class DataDictionaryTypeController {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    /**
     * 添加数据字典分类<br/>
     * 通过该接口, 可以添加新的数据字典分类。
     * @param ddt
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public DataDictionaryType create(@RequestBody DataDictionaryType ddt) {
        return this.dataDictionaryService.save(ddt);
    }

    @RequestMapping(value = "/{code}",method = RequestMethod.PUT)
    @ResponseBody
    public DataDictionaryType update(@PathVariable("code") String code,@RequestBody DataDictionaryType ddt) {
        ddt.setCode(code);
        return this.dataDictionaryService.update(ddt);
    }

    /**
     * 获取数据字典分类<br/>
     * 通过该接口, 可以获取 code 对应的字典分类信息。
     * @param code
     * @return
     */
    @RequestMapping(value = "/{code}", method = RequestMethod.GET)
    @ResponseBody
    public DataDictionaryType view(@PathVariable("code") String code) {
        return this.dataDictionaryService.getDataDictionaryType(code);
    }

    /**
     * 查询数据字典分类<br/>
     * 通过该接口, 可以筛选需要的数据字典分类信息。
     * @param pager
     * @param filters
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<DataDictionaryType> search(Pager<DataDictionaryType> pager, List<PropertyFilter> filters) {
        return this.dataDictionaryService.findDataDictionaryTypePager(pager, filters);
    }

    /** 删除数据字典分类 - 通过该接口删除数据字典分类 **/
    @RequestMapping(value = "/{code}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("code") String code) {
        this.dataDictionaryService.deleteType(code);
    }

    /**
     * 获取数据字典分类下的字典项<br/>
     * 通过该接口, 可以获取 code 对应的所有字典信息。
     * @param code
     * @return
     */
    @RequestMapping(value = "/{code}/dds", method = RequestMethod.GET)
    @ResponseBody
    public List<DataDictionary> dds(@PathVariable("code") String code) {
        return this.dataDictionaryService.find(Restrictions.eq("type", code));
    }

    /**
     * 在数据字典分类下添加数据字典项<br/>
     * 通过该接口, 可以添加新的数据项。
     * @param code
     * @param dd
     * @return
     */
    @RequestMapping(value = "/{code}/dds", method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public DataDictionary dds(@PathVariable("code") String code, @RequestBody DataDictionary dd) {
        dd.setType(code);
        return this.dataDictionaryService.save(dd);
    }
}
