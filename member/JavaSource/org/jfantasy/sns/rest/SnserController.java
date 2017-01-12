package org.jfantasy.sns.rest;

import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.sns.bean.Snser;
import org.jfantasy.sns.rest.models.BindForm;
import org.jfantasy.sns.service.SnserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 授权登录的第三方帐号。
 */
@RestController
@RequestMapping("/members/{id}/snsers")
public class SnserController {

    private final SnserService snserService;

    @Autowired
    public SnserController(SnserService snserService) {
        this.snserService = snserService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Snser> search(@PathVariable("id") Long id) {
        return this.snserService.snsers(id);
    }

    /**
     * 绑定社交账户
     *
     * @param form BindForm
     * @return Account
     */
    @RequestMapping(method = RequestMethod.POST)
    public Snser create(@PathVariable("id") Long id, @Validated(RESTful.POST.class) @RequestBody BindForm form) {
        return this.snserService.save(id, form.getType(), form.getAppId(), form.getOpenId());
    }

    /**
     * 解绑社交账户
     *
     * @param id Long
     */
    @DeleteMapping("/{snserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id,@PathVariable("snserId") Long snserId) {
        this.snserService.deltele(id,snserId);
    }

}
