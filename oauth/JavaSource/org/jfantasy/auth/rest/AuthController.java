package org.jfantasy.auth.rest;

import org.jfantasy.auth.rest.models.LoginForm;
import org.jfantasy.auth.rest.models.LogoutForm;
import org.jfantasy.auth.rest.models.Scope;
import org.jfantasy.auth.rest.models.SnsLoginForm;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.ValidationException;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.service.MemberService;
import org.jfantasy.security.bean.User;
import org.jfantasy.security.service.UserService;
import org.jfantasy.sns.bean.Snser;
import org.jfantasy.sns.bean.enums.PlatformType;
import org.jfantasy.sns.service.SnserService;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.framework.core.Openapi;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.jfantasy.weixin.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

/**
 * 用于处理登陆及退出
 */

/**
 * 用户登录退出
 **/
@RestController
public class AuthController {

    private final UserService userService;
    private final MemberService memberService;
    private final SnserService snserService;
    private final FansService fansService;
    private final WeixinSessionFactory weixinSessionFactory;

    @Autowired
    public AuthController(MemberService memberService, UserService userService, SnserService snserService, FansService fansService, WeixinSessionFactory weixinSessionFactory) {
        this.memberService = memberService;
        this.userService = userService;
        this.snserService = snserService;
        this.fansService = fansService;
        this.weixinSessionFactory = weixinSessionFactory;
    }

    /**
     * 用户登录 - 用户登录接口
     *
     * @param loginForm 登陆表单
     * @return Object
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @JsonResultFilter(ignore = {
            @IgnoreProperty(pojo = Member.class, name = {Member.BASE_JSONFIELDS})
    })
    @ResponseBody
    public Object login(@Validated(RESTful.POST.class) @RequestBody LoginForm loginForm) {
        if (StringUtil.isBlank(loginForm.getUserType())) {
            loginForm.setUserType(Scope.member == loginForm.getScope() ? Member.MEMBER_TYPE_PERSONAL : null);
        }
        switch (loginForm.getScope()) {
            case user:
                return userLogin(loginForm);
            case member:
                return memberLogin(loginForm);
            default:
                throw new RestException("不能识别的 scope 类型");
        }
    }

    /**
     * 通过第三方平台登陆
     *
     * @return Member
     */
    @PostMapping("/snslogin")
    @ResponseBody
    public Object snslogin(@RequestBody SnsLoginForm login, HttpServletResponse response) throws WeixinException {
        if (login.getType() == PlatformType.WeChat) {
            try {
                WeixinSession weixinSession = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(login.getAppId()));
                Openapi openapi = weixinSession.getOpenapi();
                Fans fans = this.fansService.save(login.getAppId(), openapi.getUser(login.getCode()));
                Snser snser = this.snserService.get(login.getType(), login.getAppId(), fans.getOpenId());
                if (snser == null) {
                    response.setStatus(422);
                    return fans;
                }
                Member member = this.memberService.login(snser.getMember().getUsername());
                return validateUserType(member, login.getUserType());
            } finally {
                WeixinSessionUtils.closeSession();
            }
        }
        throw new ValidationException("不支持该平台登陆");
    }

    private User userLogin(LoginForm loginForm) {
        User user = this.userService.login(loginForm.getUsername(), loginForm.getPassword());
        return validateUserType(user, loginForm.getUserType());
    }

    private User validateUserType(User user, String userType) {
        if (StringUtil.isNotBlank(userType) && !userType.equals(user.getUserType())) {
            throw new RestException("UserType 不一致");
        }
        return user;
    }

    private Member memberLogin(LoginForm loginForm) {
        Member member = memberService.login(loginForm.getType(), loginForm.getUsername(), loginForm.getPassword());
        return validateUserType(member, loginForm.getUserType());
    }

    private Member validateUserType(Member user, String userType) {
        if (StringUtil.isNotBlank(userType) && !userType.equals(user.getType())) {
            throw new RestException("UserType 不一致");
        }
        return user;
    }

    /**
     * 用户登出 - 用户登出接口
     *
     * @param loginForm 退出表达
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@RequestBody LogoutForm loginForm) {
        switch (loginForm.getScope()) {
            case user:
                this.userService.logout(loginForm.getUsername());
                break;
            case member:
                memberService.logout(loginForm.getUsername());
                break;
            default:
                throw new RestException("不能识别的 scope 类型");
        }
    }


}
