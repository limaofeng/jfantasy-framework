package org.jfantasy.weixin.rest;

import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.weixin.bean.Fans;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.service.FansService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 微信公众号粉丝管理接口
 */
@RestController
@RequestMapping("/weixin/apps/{appid}/fans")
public class FansController {

    private final FansService fansService;
    private final WeixinSessionFactory weixinSessionFactory;

    @Autowired
    public FansController(FansService fansService, WeixinSessionFactory weixinSessionFactory) {
        this.fansService = fansService;
        this.weixinSessionFactory = weixinSessionFactory;
    }

    /**
     * 获取微信粉丝 - 通过粉丝ID获取关注的用户信息
     *
     * @param appid  APPID
     * @param openid OPENID
     * @return User
     * @throws WeixinException 微信异常
     */
    @RequestMapping(value = "/{openid}", method = RequestMethod.GET)
    @ResponseBody
    public Fans view(@PathVariable("appid") String appid, @PathVariable String openid) throws WeixinException {
        try {
            WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(appid));

            Fans user = fansService.checkCreateMember(appid, openid);
            if (user == null) {
                throw new NotFoundException("未找到对应的粉丝信息");
            }
            return user;

        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

    /**
     * 获取微信粉丝 - 通过粉丝ID获取关注的用户信息
     **/
    @RequestMapping(value = "/{openid}", method = RequestMethod.PATCH)
    @ResponseBody
    public Fans update(@PathVariable("appid") String appid, @PathVariable String openid, @RequestBody Fans user) throws WeixinException, IOException {
        user.setAppId(appid);
        user.setOpenId(openid);
        return fansService.save(user);
    }

}
