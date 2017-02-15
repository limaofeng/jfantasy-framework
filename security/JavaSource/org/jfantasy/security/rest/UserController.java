package org.jfantasy.security.rest;


import io.swagger.annotations.ApiImplicitParam;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.hateoas.ResultResourceSupport;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.web.WebUtil;
import org.jfantasy.security.bean.Menu;
import org.jfantasy.security.bean.Role;
import org.jfantasy.security.bean.User;
import org.jfantasy.security.bean.UserDetails;
import org.jfantasy.security.bean.enums.UserType;
import org.jfantasy.security.rest.models.PasswordForm;
import org.jfantasy.security.rest.models.assembler.UserResourceAssembler;
import org.jfantasy.security.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;


@RestController
@RequestMapping("/users")
public class UserController {

    public static UserResourceAssembler assembler = new UserResourceAssembler();

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 查询用户
     *
     * @param pager
     * @param filters
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    @ApiImplicitParam(value = "filters", name = "filters", paramType = "query", dataType = "string")
    public Pager<ResultResourceSupport> search(Pager<User> pager, List<PropertyFilter> filters) {
        return assembler.toResources(this.userService.findPager(pager, filters));
    }

    /**
     * 获取用户
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResultResourceSupport view(@PathVariable("id") Long id) {
        return assembler.toResource(this.userService.get(id));
    }

    @JsonResultFilter(
            ignore = @IgnoreProperty(pojo = User.class, name = {"password", "enabled", "account_nonExpired", "accountNonLocked", "credentialsNonExpired"}),
            allow = @AllowProperty(pojo = UserDetails.class, name = {"name", "sex", "birthday", "avatar"})
    )
    @RequestMapping(value = "/{id}/password", method = RequestMethod.PUT)
    @ResponseBody
    public ResultResourceSupport password(@PathVariable("id") Long id, @RequestBody PasswordForm form) {
        return assembler.toResource(this.userService.changePassword(id, form.getOldPassword(), form.getNewPassword()));
    }

    /**
     * 获取用户的详细信息
     *
     * @param id UID
     * @return Details
     */
    @RequestMapping(value = "/{id}/profile", method = RequestMethod.GET)
    public Object profile(@PathVariable("id") Long id) {
        User user = this.get(id);
        if (user.getUserType() == UserType.employee) {
            return user.getEmployee();
        } else {
            return user.getDetails();
        }
    }

    /**
     * 添加用户
     *
     * @param user
     * @return
     */
    @RequestMapping(method = {RequestMethod.POST})
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public ResultResourceSupport create(@Validated(RESTful.POST.class) @RequestBody User user) {
        return assembler.toResource(this.userService.save(user));
    }

    /**
     * 删除用户
     *
     * @param id
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.DELETE})
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) {
        this.userService.delete(id);
    }

    /**
     * 更新用户
     *
     * @param id
     * @param user
     * @return
     */
    @RequestMapping(value = "/{id}", method = {RequestMethod.PATCH, RequestMethod.POST})
    public ResultResourceSupport update(@PathVariable("id") Long id, @RequestBody User user, HttpServletRequest request) {
        user.setId(id);
        return assembler.toResource(this.userService.update(user, WebUtil.hasMethod(request, HttpMethod.PATCH.name())));
    }

    /**
     * 获取用户授权的菜单信息
     *
     * @param id UID
     * @return Set<Menu>
     */
    @RequestMapping(value = "/{id}/menus", method = {RequestMethod.GET})
    @ResponseBody
    public Set<Menu> menus(@PathVariable("id") Long id) {
        User user = this.get(id);
        return user.getAllMenus();
    }

    /**
     * 获取用户的角色
     *
     * @param id UID
     * @return Set<Role>
     */
    @GetMapping("/{id}/roles")
    @ResponseBody
    public Set<Role> roles(@PathVariable("id") Long id) {
        User user = this.get(id);
        return user.getAllRoles();
    }

    @RequestMapping(value = "/{id}/roles", method = {RequestMethod.POST, RequestMethod.PATCH})
    @ResponseBody
    public List<Role> roles(@PathVariable("id") Long id, @RequestBody String[] roles, HttpServletRequest request) {
        return userService.addRoles(id, WebUtil.hasMethod(request, HttpMethod.POST.name()), roles);
    }

    @DeleteMapping("/{id}/roles")
    @ResponseBody
    public List<Role> rroles(@PathVariable("id") Long id, @RequestParam(value = "role") String[] roles) {
        return userService.removeRoles(id, roles);
    }

    private User get(Long id) {
        return this.userService.get(id);
    }

}
