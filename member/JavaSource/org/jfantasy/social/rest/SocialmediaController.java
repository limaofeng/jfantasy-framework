package org.jfantasy.social.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.social.bean.Socialmedia;
import org.jfantasy.social.service.SocialmediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/socialmedias")
public class SocialmediaController {

    private final SocialmediaService socialmediaService;

    @Autowired
    public SocialmediaController(SocialmediaService socialmediaService) {
        this.socialmediaService = socialmediaService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Socialmedia> search(Pager<Socialmedia> pager, List<PropertyFilter> filters) {
        return this.socialmediaService.findPager(pager, filters);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Socialmedia view(@PathVariable("id") Long id) {
        return this.get(id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Socialmedia create(@Validated(RESTful.POST.class) @RequestBody Socialmedia socialmedia) {
        return this.socialmediaService.save(socialmedia);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public Socialmedia update(@PathVariable("id") Long id, @RequestBody Socialmedia socialmedia, HttpServletRequest request) {
        socialmedia.setId(id);
        return this.socialmediaService.update(socialmedia, WebUtil.has(request, RequestMethod.PATCH));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.socialmediaService.deltele(id);
    }

    private Socialmedia get(Long id) {
        Socialmedia socialmedia = this.socialmediaService.get(id);
        if (socialmedia == null) {
            throw new NotFoundException("[id =" + id + "]对应的社交媒体不存在");
        }
        return socialmedia;
    }

}
