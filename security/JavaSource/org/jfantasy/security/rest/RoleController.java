package org.jfantasy.security.rest;

import io.swagger.annotations.ApiImplicitParam;
import org.hibernate.criterion.Restrictions;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.security.bean.Menu;
import org.jfantasy.security.bean.Permission;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.service.PermissionService;
import org.jfantasy.security.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/roles")
public class RoleController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private PermissionService permissionService;

    /**
     * 按条件角色
     *
     * @param pager   翻页对象
     * @param filters 筛选
     * @return Pager
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<Role> search(Pager<Role> pager, List<PropertyFilter> filters) {
        return this.roleService.findPager(pager, filters);
    }

    @RequestMapping(value = "/{id}", method = {RequestMethod.GET})
    @ResponseBody
    public Role view(@PathVariable("id") String id) {
        return roleService.get(id);
    }

    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public Role create(@RequestBody Role role) {
        return roleService.save(role);
    }

    @RequestMapping(value = "/{id}",  method = {RequestMethod.POST, RequestMethod.PATCH})
    @ResponseBody
    public Role update(@PathVariable("id") String id, @RequestBody Role role,HttpServletRequest request) {
        role.setId(id);
        return roleService.update(role,WebUtil.hasMethod(request,HttpMethod.PATCH.name()));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") String id) {
        this.roleService.delete(id);
    }

    @RequestMapping(method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@RequestBody String... ids) {
        this.roleService.delete(ids);
    }

    /**
     * 返回角色的授权菜单
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/menus", method = {RequestMethod.GET})
    @ResponseBody
    public String[] menus(@PathVariable("id") String id) {
        return ObjectUtil.toFieldArray(get(id).getMenus(), "id", String.class);
    }

    /**
     * 更新角色菜单权限
     *
     * @param id
     * @param menuIds
     * @return
     */
    @RequestMapping(value = "/{id}/menus", method = {RequestMethod.POST, RequestMethod.PATCH})
    @ResponseBody
    public List<Menu> menus(@PathVariable("id") String id, @RequestBody String[] menuIds, HttpServletRequest request) {
        return this.roleService.addMenus(id,WebUtil.hasMethod(request, HttpMethod.POST.name()),menuIds);
    }

    @RequestMapping(value = "/{id}/menus", method = RequestMethod.DELETE)
    @ResponseBody
    public List<Menu> menus(@PathVariable("id") String id, @RequestParam(value = "id") String[] menuIds) {
        return this.roleService.removeMenus(id,menuIds);
    }

    /**
     * 返回角色权限
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}/permissions", method = {RequestMethod.GET})
    @ResponseBody
    public List<Permission> permissions(@PathVariable("id") String id) {
        return permissionService.find(Restrictions.eq("roles.id", id));
    }

    /**
     * 为角色添加权限
     *
     * @param id
     * @param permissionId
     * @return
     */
    @RequestMapping(value = "/{id}/permissions", method = {RequestMethod.POST})
    @ResponseBody
    public List<Permission> permissions(@PathVariable("id") String id, @RequestBody Long... permissionId) {
        throw new RuntimeException("该方法未实现!");
    }

    private Role get(String id) {
        return this.roleService.get(id);
    }

}