package org.jfantasy.sns.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.sns.bean.Platform;
import org.jfantasy.sns.service.PlatformService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/platforms")
public class PlatformController {

    private final PlatformService platformService;

    @Autowired
    public PlatformController(PlatformService platformService) {
        this.platformService = platformService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Platform> search(Pager<Platform> pager, List<PropertyFilter> filters) {
        return this.platformService.findPager(pager, filters);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Platform view(@PathVariable("id") Long id) {
        return this.get(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Platform create(@Validated(RESTful.POST.class) @RequestBody Platform platform) {
        return this.platformService.save(platform);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Platform update(@PathVariable("id") Long id, @RequestBody Platform platform, HttpServletRequest request) {
        platform.setId(id);
        return this.platformService.update(platform, WebUtil.has(request, RequestMethod.PATCH));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.platformService.deltele(id);
    }

    private Platform get(Long id) {
        Platform platform = this.platformService.get(id);
        if (platform == null) {
            throw new NotFoundException("[id =" + id + "]对应的社交媒体不存在");
        }
        return platform;
    }

}
