package org.jfantasy.security.rest;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.security.bean.Menu;
import org.jfantasy.security.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "security-menus", description = "菜单")
@RestController
@RequestMapping("/security/menus")
public class MenuController {

    @Autowired
    private transient MenuService menuService;

    @ApiOperation(value = "查询菜单", notes = "筛选文章，返回菜单数组", response = Menu[].class)
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public Pager<Menu> search(Pager<Menu> pager, List<PropertyFilter> filters) {
        if (!pager.isOrderBySetted()) {
            pager.setOrders(Pager.Order.asc);
            pager.setOrderBy("sort");
        }
        return this.menuService.findPager(pager, filters);
    }

    @ApiOperation(value = "获取菜单")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public Menu view(@PathVariable("id") Long id) {
        return this.menuService.get(id);
    }

    @ApiOperation(value = "删除菜单")
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.menuService.delete(id);
    }

    @ApiOperation(value = "批量删除菜单")
    @RequestMapping(method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody Long... id) {
        this.menuService.delete(id);
    }

    @ApiOperation(value = "添加菜单")
    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Menu create(@RequestBody Menu menu) {
        return menuService.save(menu);
    }

    @ApiOperation(value = "更新菜单")
    @RequestMapping(value = "/{id}", method = {RequestMethod.PUT})
    @ResponseBody
    public Menu update(@PathVariable("id") Long id, @RequestBody Menu menu) {
        menu.setId(id);
        return menuService.save(menu);
    }

}
