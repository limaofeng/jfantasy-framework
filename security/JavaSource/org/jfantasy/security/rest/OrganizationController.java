package org.jfantasy.security.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.security.bean.Organization;
import org.jfantasy.security.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/orgs")
public class OrganizationController {

    private final OrganizationService organizationService;

    @Autowired
    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Organization> search(Pager<Organization> pager, List<PropertyFilter> filters) {
        return this.organizationService.findPager(pager, filters);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Organization view(@PathVariable("id") String id) {
        return organizationService.get(id);
    }

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Organization create(@RequestBody Organization org) {
        return organizationService.save(org);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @ResponseBody
    public Organization update(@PathVariable("id") String id, @RequestBody Organization org, HttpServletRequest request) {
        org.setId(id);
        return organizationService.update(org, WebUtil.hasMethod(request,RequestMethod.PATCH.name()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.organizationService.delete(id);
    }

}
