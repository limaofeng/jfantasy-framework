package org.jfantasy.contacts.service;

import org.jfantasy.contacts.bean.Book;
import org.jfantasy.contacts.bean.Group;
import org.jfantasy.contacts.bean.Linkman;
import org.jfantasy.contacts.dao.BookDao;
import org.jfantasy.contacts.dao.GroupDao;
import org.jfantasy.contacts.dao.LinkmanDao;
import org.jfantasy.contacts.listener.AddressBookListener;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.security.SpringSecurityUtils;
import org.jfantasy.security.userdetails.SimpleUser;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
@Transactional
public class AddressBookService {

    @Autowired
    private BookDao bookDao;
    @Autowired
    private GroupDao groupDao;
    @Autowired
    private LinkmanDao linkmanDao;

    public AddressBook getAddressBook(String owner, String ownerType) {
        Book book = bookDao.findUnique(Restrictions.eq("ownerType", ownerType), Restrictions.eq("owner", owner));
        if (book == null) {
            book = new Book(owner, ownerType);
            book = bookDao.save(book);
        }
        return new AddressBook(book);
    }

    public Pager<Linkman> findPager(Pager<Linkman> pager, List<PropertyFilter> filters) {
        return this.linkmanDao.findPager(pager, filters);
    }

    public AddressBook myBook(String username) {
        return getAddressBook(username, "1");
    }

    public Group save(Group group) {
        return groupDao.save(group);
    }

    public void deleteGroup(Long... ids) {
        for (Long id : ids) {
            groupDao.delete(id);
        }
    }

    public Linkman save(Linkman linkman) {
        return linkmanDao.save(linkman);
    }

    public void deleteLinkman(Long... ids) {
        for (Long id : ids) {
            linkmanDao.delete(id);
        }
    }

    public Linkman get(Long id) {
        return this.linkmanDao.get(id);
    }

    public static List<Group> getGroups() {
        return ((AddressBook) SpringSecurityUtils.getCurrentUser(SimpleUser.class).data(AddressBookListener.CURRENT_USER_BOOK_KEY)).getGroups();
    }

}
