package org.jfantasy.oauth.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.oauth.bean.ApiKey;
import org.jfantasy.oauth.bean.Application;
import org.jfantasy.oauth.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 授权登录的第三方帐号。
 */
@RestController("oauth.appController")
@RequestMapping("/apps")
public class AppController {

    private final AppService appService;

    @Autowired
    public AppController(AppService appService) {
        this.appService = appService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Application> search(Pager<Application> pager, List<PropertyFilter> filters) {
        return this.appService.findPager(pager, filters);
    }

    /**
     * 新增APP
     *
     * @param app Application
     * @return Application
     */
    @RequestMapping(method = RequestMethod.POST)
    public Application create(@Validated(RESTful.POST.class) @RequestBody Application app) {
        return this.appService.save(app);
    }

    @PostMapping("/{id}/apikeys")
    public ApiKey apikeys(@PathVariable("id") Long id, @RequestBody ApiKey apiKey) {
        return this.appService.save(id,apiKey);
    }

    @GetMapping("/{id}/apikeys")
    public List<ApiKey> apikeys(@PathVariable("id") Long id) {
        return this.appService.find(id);
    }

    /**
     * 删除
     *
     * @param id Long
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.appService.deltele(id);
    }

}
