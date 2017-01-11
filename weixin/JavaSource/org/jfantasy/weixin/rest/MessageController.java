package org.jfantasy.weixin.rest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.weixin.framework.core.WeixinCoreHelper;
import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.factory.WeixinSessionFactory;
import org.jfantasy.weixin.framework.factory.WeixinSessionUtils;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * 微信消息推送接口
 */
@RestController
@RequestMapping("/weixin/apps/{appid}/messages")
public class MessageController {

    private static final Log LOG = LogFactory.getLog(MessageController.class);

    private final WeixinSessionFactory weixinSessionFactory;

    @Autowired
    public MessageController(WeixinSessionFactory weixinSessionFactory) {
        this.weixinSessionFactory = weixinSessionFactory;
    }

    /**
     * 微信消息接口 - 接口接收微信公众平台推送的微信消息及事件,直接调用无效
     **/
    @RequestMapping(value = "/push", method = {RequestMethod.GET, RequestMethod.POST})
    @ResponseBody
    public String push(@PathVariable("appid") String appid, HttpServletRequest request) throws IOException {
        String echostr = request.getParameter("echostr");
        if (StringUtils.isNotBlank(echostr)) {
            // 说明是一个仅仅用来验证的请求，回显echostr
            return echostr;
        }
        //解析数据
        //打开session,并保存到上下文
        try {
            WeixinSession session = WeixinSessionUtils.saveSession(weixinSessionFactory.openSession(appid));
            WeixinCoreHelper helper = weixinSessionFactory.getWeixinCoreHelper();
            WeixinMessage message = helper.parseInMessage(session, request);

            WeixinMessage returnMessage = weixinSessionFactory.execute(message);
            if (returnMessage == null) {
                return "";
            }
            String outMessage = helper.buildOutMessage(session, request.getParameter("encrypt_type"), returnMessage);
            if (LOG.isDebugEnabled()) {
                LOG.debug("outMessage=" + outMessage);
            }
            return outMessage;
        } catch (WeixinException e) {
            LOG.error(e.getMessage(), e);
            return e.getMessage();
        } finally {
            WeixinSessionUtils.closeSession();
        }
    }

}
