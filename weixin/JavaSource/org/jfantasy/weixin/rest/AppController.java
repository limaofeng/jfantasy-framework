package org.jfantasy.weixin.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.weixin.bean.App;
import org.jfantasy.weixin.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 微信公众账号
 */
@RestController
@RequestMapping("/weixin/apps")
public class AppController {

    private final AppService appService;

    @Autowired
    public AppController(AppService accountService) {
        this.appService = accountService;
    }

    /**
     * 查询微信公众账号 - 筛选微信公众账号，返回通用分页对象
     **/
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<App> search(Pager<App> pager, List<PropertyFilter> filters) {
        return this.appService.findPager(pager, filters);
    }

    /**
     * 获取微信公众账号
     *
     * @param id ID
     * @return App
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public App view(@PathVariable("id") String id) {
        return this.appService.get(id);
    }

    /**
     * 添加微信公众账号
     *
     * @param app 微信APP
     * @return App
     */
    @RequestMapping(method = RequestMethod.POST)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public App create(@RequestBody App app) {
        return this.appService.save(app);
    }

    /**
     * 更新微信公众账号
     *
     * @param id  ID
     * @param app 微信APP
     * @return App
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.PATCH)
    @ResponseBody
    public App update(@PathVariable("id") String id, @RequestBody App app) {
        app.setId(id);
        return this.appService.save(app);
    }

    /**
     * 删除微信公众账号
     *
     * @param id ID
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.appService.delete(id);
    }

}
