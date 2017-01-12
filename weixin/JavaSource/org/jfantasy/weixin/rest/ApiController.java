package org.jfantasy.weixin.rest;

import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.message.content.Menu;
import org.jfantasy.weixin.framework.message.user.User;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/weixin/apps/{id}/apis")
public class ApiController {

    private final WeixinSessionFactory weixinSessionFactory;

    @Autowired
    public ApiController(WeixinSessionFactory weixinSessionFactory) {
        this.weixinSessionFactory = weixinSessionFactory;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    @ResponseBody
    public List<User> users(@PathVariable("id") String id) throws WeixinException {
        try {
            WeixinSession session = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(id));
            return session.getUsers();
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

    @RequestMapping(value = "/menus", method = RequestMethod.GET)
    @ResponseBody
    public List<Menu> menus(@PathVariable("id") String id, @RequestParam(value = "url") String url) throws WeixinException {
        try {
            WeixinSession session = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(id));
            return session.getMenus();
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

}
