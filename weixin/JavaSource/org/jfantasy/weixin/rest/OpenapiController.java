package org.jfantasy.weixin.rest;

import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.framework.core.Openapi;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.message.user.User;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.jfantasy.weixin.rest.models.OAuth2UrlForm;
import org.jfantasy.weixin.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weixin/apps/{id}/openapis")
public class OpenapiController {

    private final WeixinSessionFactory weixinSessionFactory;
    private final FansService fansService;

    @Autowired
    public OpenapiController(WeixinSessionFactory weixinSessionFactory, FansService fansService) {
        this.weixinSessionFactory = weixinSessionFactory;
        this.fansService = fansService;
    }

    /**
     * 通过 oauth2 code 获取微信粉丝
     *
     * @param id   id
     * @param code 授权码
     * @return User
     * @throws WeixinException 微信异常
     */
    @RequestMapping(value = "/userinfo", method = RequestMethod.GET)
    public Fans userinfo(@PathVariable("id") String id, @RequestParam("code") String code) throws WeixinException {
        try {
            WeixinSession weixinSession = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(id));
            Openapi openapi = weixinSession.getOpenapi();
            User user = openapi.getUser(code);
            if (user == null) {
                throw new NotFoundException("未找到对应的粉丝信息");
            }
            return fansService.save(id, user);
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

    @PostMapping("/oauth2url")
    @ResponseBody
    public String userinfo(@PathVariable("id") String id, @RequestBody OAuth2UrlForm form) throws WeixinException {
        try {
            WeixinSession weixinSession = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(id));
            Openapi openapi = weixinSession.getOpenapi();
            return openapi.getAuthorizationUrl(form.getRedirectUri(), form.getScope(), form.getState());
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

}
