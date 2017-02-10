package org.jfantasy.security.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.security.bean.Job;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    @Autowired
    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Job> search(Pager<Job> pager, List<PropertyFilter> filters) {
        return this.jobService.findPager(pager, filters);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Job view(@PathVariable("id") String id) {
        return jobService.get(id);
    }

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Job create(@RequestBody Job job) {
        return jobService.save(job);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.PUT})
    @ResponseBody
    public Job update(@PathVariable("id") String id, @RequestBody Job job, HttpServletRequest request) {
        job.setId(id);
        return jobService.update(job, WebUtil.hasMethod(request,RequestMethod.PATCH.name()));
    }

    @GetMapping("/{id}/roles")
    @ResponseBody
    public List<Role> roles(@PathVariable("id") String id) {
        return jobService.get(id).getRoles();
    }

    @PostMapping("/{id}/roles")
    @ResponseBody
    public List<Role> roles(@PathVariable("id") String id, @RequestBody String... roles) {
        return jobService.addRoles(id,roles);
    }

    @DeleteMapping("/{id}/roles")
    @ResponseBody
    public List<Role> rroles(@PathVariable("id") String id, @RequestBody String... roles) {
        return jobService.removeRoles(id,roles);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.jobService.delete(id);
    }

}
