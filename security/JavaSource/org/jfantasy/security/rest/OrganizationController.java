package org.jfantasy.security.rest;

import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.security.bean.Organization;
import org.jfantasy.security.service.OrganizationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    public List<Organization> search(List<PropertyFilter> filters) {
        return this.organizationService.findPager(filters);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Organization view(@PathVariable("id") String id) {
        return get(id);
    }

    @RequestMapping(value = "/{id}/jobs", method = {RequestMethod.GET})
    @ResponseBody
    public ModelAndView jobs(@PathVariable("id") String id) {
        Organization org = get(id);
        return new ModelAndView("redirect:/jobs?EQS_organization.id=" + org.getId());
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
        return organizationService.update(org, WebUtil.hasMethod(request, RequestMethod.PATCH.name()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.organizationService.delete(id);
    }

    private Organization get(String id) {
        Organization org = this.organizationService.get(id);
        if (org == null) {
            throw new NotFoundException("[ID=" + id + "]的机构不存在");
        }
        return org;
    }

}
