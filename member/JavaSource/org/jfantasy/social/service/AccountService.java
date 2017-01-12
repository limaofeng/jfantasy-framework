package org.jfantasy.social.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.social.bean.Account;
import org.jfantasy.social.dao.AccountDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AccountService {

    private final AccountDao accountDao;

    @Autowired
    public AccountService(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Transactional(readOnly = true)
    public Pager<Account> findPager(Pager<Account> pager, List<PropertyFilter> filters) {
        return this.accountDao.findPager(pager, filters);
    }

    @Transactional(readOnly = true)
    public Account get(Long id) {
        return this.accountDao.get(id);
    }

    @Transactional
    public Account save(Account account) {
        return this.accountDao.save(account);
    }

    @Transactional
    public Account update(Account account, boolean patch) {
        return this.accountDao.update(account, patch);
    }

    @Transactional
    public void deltele(Long id) {
        this.accountDao.delete(id);
    }

}
