package org.jfantasy.weixin.rest;

import org.jfantasy.weixin.framework.core.Jsapi;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/weixin/apps/{id}/jsapis")
public class JsapiController {

    private final WeixinSessionFactory weixinSessionFactory;

    @Autowired
    public JsapiController(WeixinSessionFactory weixinSessionFactory) {
        this.weixinSessionFactory = weixinSessionFactory;
    }

    /**
     * 获取 jsticket
     *
     * @param id ID
     * @return String
     * @throws WeixinException 微信异常
     */
    @RequestMapping(value = "/ticket", method = RequestMethod.GET)
    @ResponseBody
    public String getTicket(@PathVariable("id") String id) throws WeixinException {
        try {
            WeixinSession session = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(id));
            Jsapi jsapi = session.getJsapi();
            if (jsapi == null) {
                throw new WeixinException(" jsapi is null ");
            }
            return jsapi.getTicket();
        } finally {
            WeixinSessionUtils.closeSession();
        }

    }

    /**
     * 获取 url 签名
     *
     * @param id  ID
     * @param url URL
     * @return Signature
     * @throws WeixinException 微信异常
     */
    @RequestMapping(value = "/signature", method = RequestMethod.GET)
    @ResponseBody
    public Jsapi.Signature signature(@PathVariable("id") String id, @RequestParam(value = "url") String url) throws WeixinException {
        try {
            WeixinSession session = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(id));
            Jsapi jsapi = session.getJsapi();
            if (jsapi == null) {
                throw new WeixinException(" jsapi is null ");
            }
            return jsapi.signature(url);
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

}
