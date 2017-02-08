package org.jfantasy.security.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.security.bean.Menu;
import org.jfantasy.security.dao.MenuDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class MenuService {

    private final MenuDao menuDao;

    private static final Log LOGGER = LogFactory.getLog(MenuService.class);

    @Autowired
    public MenuService(MenuDao menuDao) {
        this.menuDao = menuDao;
    }

    private void sort(Menu menu, List<Menu> siblings) {
        Menu old = menu.getId() != null ? this.menuDao.get(menu.getId()) : null;
        if (old == null) {//新增数据
            menu.setSort(siblings.size() + 1);
            return;
        }
        if (menu.getSort() != null && (ObjectUtil.find(siblings, "id", old.getId()) == null || !menu.getSort().equals(old.getSort()))) {//更新数据
            if (ObjectUtil.find(siblings, "id", old.getId()) == null) {//移动了节点的层级
                int i = 0;
                for (Menu m : ObjectUtil.sort((old.getParent() == null || StringUtil.isBlank(old.getParent().getId())) ? menuDao.find(Restrictions.isNull("parent")) : menuDao.findBy("parent.id", old.getParent().getId()), "sort", "asc")) {
                    m.setSort(i++);
                    this.menuDao.save(m);
                }
                siblings.add(menu.getSort() - 1, menu);
            } else {
                Menu t = ObjectUtil.remove(siblings, "id", old.getId());
                if (siblings.size() >= menu.getSort()) {
                    siblings.add(menu.getSort() - 1, t);
                } else {
                    siblings.add(t);
                }
            }
            //重新排序后更新新的位置
            for (int i = 0; i < siblings.size(); i++) {
                Menu m = siblings.get(i);
                if (m.getId().equals(menu.getId())) {
                    continue;
                }
                m.setSort(i + 1);
                this.menuDao.save(m);
            }
        } else {
            menu.setSort(old.getSort());
        }
    }

    private List<Menu> parent(Menu menu) {
        if (menu.getParent() == null || StringUtil.isBlank(menu.getParent().getId())) {
            menu.setLayer(1);
            menu.setParent(null);
            return ObjectUtil.sort(menuDao.find(Restrictions.isNull("parent")), "sort", "asc");
        } else {
            Menu parentMenu = this.get(menu.getParent().getId());
            menu.setLayer(parentMenu.getLayer() + 1);
            menu.setParent(parentMenu);
            return ObjectUtil.sort(menuDao.findBy("parent.id", parentMenu.getId()), "sort", "asc");
        }
    }

    private void preset(Menu menu) {
        // 设置 parent
        List<Menu> menus = parent(menu);
        // 设置 sort
        sort(menu, menus);
        // 设置 path
        if (menu.getParent() == null) {
            menu.setPath(Menu.PATH_SEPARATOR + menu.getId());
        } else {
            menu.setPath(menu.getParent().getPath() + Menu.PATH_SEPARATOR + menu.getId());
        }
    }

    public Menu save(Menu menu) {
        preset(menu);
        this.menuDao.save(menu);
        return menu;
    }

    public Menu update(Menu menu, boolean patch) {
        preset(menu);
        return this.menuDao.save(menu, patch);
    }

    public void delete(String... ids) {
        for (String id : ids) {
            this.menuDao.delete(id);
        }
    }

    public Menu get(String id) {
        return this.menuDao.get(id);
    }

    public Pager<Menu> findPager(Pager<Menu> pager, List<PropertyFilter> filters) {
        return menuDao.findPager(pager, filters);
    }

    public List<Menu> list(Criterion... criterions) {
        return this.menuDao.find(criterions);
    }

    public List<Menu> list(Criterion[] criterions, String orderBy, String order) {
        return this.menuDao.find(criterions, orderBy, order);
    }

}