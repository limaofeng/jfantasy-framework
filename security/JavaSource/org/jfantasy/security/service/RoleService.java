package org.jfantasy.security.service;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.security.bean.Menu;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.dao.MenuDao;
import org.jfantasy.security.dao.RoleDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleService {

    private final RoleDao roleDao;
    private final MenuDao menuDao;

    @Autowired
    public RoleService(RoleDao roleDao,MenuDao menuDao) {
        this.roleDao = roleDao;
        this.menuDao = menuDao;
    }

    public List<Role> getAll() {
        return roleDao.findBy("enabled", true);
    }

    public Pager<Role> findPager(Pager<Role> pager, List<PropertyFilter> filters) {
        return this.roleDao.findPager(pager, filters);
    }

    public Role save(Role role) {
        return roleDao.save(role);
    }

    public Role update(Role role,boolean patch) {
        return this.roleDao.update(role,patch);
    }

    public Role get(String id) {
        return this.roleDao.get(id);
    }

    public void delete(String... ids) {
        for (String code : ids) {
            this.roleDao.delete(code);
        }
    }

    public List<Menu> removeMenus(String id,String[] menuIds){
        Role role = this.roleDao.get(id);
        if (menuIds.length == 1 && "clear".equals(menuIds[0])) {
            role.getMenus().clear();
        } else {
            for (String menuId : menuIds) {
                ObjectUtil.remove(role.getMenus(), "id", menuId);
            }
        }
        return this.roleDao.update(role).getMenus();
    }

    public List<Menu> addMenus(String id, boolean clear, String[] menuIds) {
        Role role = this.get(id);
        if (clear) {
            role.getMenus().clear();
        }
        for (String menuId : menuIds) {
            if (!ObjectUtil.exists(role.getMenus(), "id", menuId)) {
                role.getMenus().add(this.menuDao.get(menuId));
            }
        }
        this.roleDao.update(role);
        return role.getMenus();
    }

}