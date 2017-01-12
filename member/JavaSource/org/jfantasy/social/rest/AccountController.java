package org.jfantasy.social.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.social.bean.Account;
import org.jfantasy.social.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 授权登录的第三方帐号。
 */
@RestController
@RequestMapping("/members/{id}/accounts")
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Account> search(Pager<Account> pager, List<PropertyFilter> filters) {
        return this.accountService.findPager(pager, filters);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Account view(@PathVariable("id") Long id) {
        return this.get(id);
    }

    /**
     * 绑定社交账户
     * @param account Account
     * @return Account
     */
    @RequestMapping(method = RequestMethod.POST)
    public Account create(@Validated(RESTful.POST.class) @RequestBody Account account) {
        return this.accountService.save(account);
    }

    /**
     * 解绑社交账户
     * @param id Long
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.accountService.deltele(id);
    }

    private Account get(Long id) {
        Account account = this.accountService.get(id);
        if (account == null) {
            throw new NotFoundException("[id =" + id + "]对应的账号信息不存在");
        }
        return account;
    }

}
