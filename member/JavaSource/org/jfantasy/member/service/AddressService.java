package org.jfantasy.member.service;

import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.member.bean.Address;
import org.jfantasy.member.dao.AddressDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    private AddressDao addressDao;

    private void changeDefault(Address address) {
        Address old = address.getId() != null ? this.addressDao.get(address.getId()) : null;
        String ownerId = old != null ? old.getOwnerId() : address.getOwnerId();
        String ownerType = old != null ? old.getOwnerType() : address.getOwnerType();

        int count = this.addressDao.count(Restrictions.eq("ownerId", ownerId),Restrictions.eq("ownerType", ownerType));
        if (count == 0) {
            address.setDefault(true);
        } else {
            if (ObjectUtil.defaultValue(address.getDefault(), false)) {
                List<Address> addresss = this.addressDao.find(Restrictions.eq("ownerId", ownerId),Restrictions.eq("ownerType", ownerType), Restrictions.eq("isDefault", true));
                for (Address ver : addresss) {
                    ver.setDefault(false);
                    addressDao.save(ver);
                }
            }
        }
    }

    @Transactional
    public Address save(Address address) {
        changeDefault(address);
        return addressDao.save(address);
    }

    @Transactional
    public Address update(Address address) {
        changeDefault(address);
        return this.addressDao.update(address);
    }

    @Transactional
    public void deltele(Long... ids) {
        this.addressDao.delete(ids);
    }

    public Address get(Long id) {
        return this.addressDao.get(id);
    }

    public Pager<Address> findPager(Pager<Address> pager, List<PropertyFilter> filters) {
        return this.addressDao.findPager(pager, filters);
    }

}
