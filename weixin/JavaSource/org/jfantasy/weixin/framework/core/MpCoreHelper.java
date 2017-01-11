package org.jfantasy.weixin.framework.core;

import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.message.content.*;
import org.jfantasy.weixin.framework.message.user.OpenIdList;
import org.jfantasy.weixin.framework.message.user.User;
import org.jfantasy.weixin.framework.oauth2.Scope;
import org.jfantasy.weixin.framework.session.WeixinSession;
import org.jfantasy.weixin.framework.session.WeixinApp;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 微信服务号与订阅号
 */
@Component
public class MpCoreHelper implements WeixinCoreHelper {

    private Map<String, WeixinDetails> weiXinDetailsMap = new HashMap<String, WeixinDetails>();

    @Override
    public void register(WeixinApp weixinApp) {
        if (weiXinDetailsMap.containsKey(weixinApp.getId())) {
            weiXinDetailsMap.remove(weixinApp.getId());
        }
        weiXinDetailsMap.put(weixinApp.getId(), new WeixinDetails(weixinApp));
    }

    @Override
    public WeixinMessage parseInMessage(WeixinSession session, HttpServletRequest request) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().parseInMessage(request);
    }

    @Override
    public String buildOutMessage(WeixinSession session, String encryptType, WeixinMessage message) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().parseInMessage(encryptType, message);
    }

    @Override
    public void sendImageMessage(WeixinSession session, Image content, String... toUsers) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendImageMessage(content, toUsers);
    }

    @Override
    public void sendImageMessage(WeixinSession session, Image content, long toGroup) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendImageMessage(content, toGroup);
    }

    @Override
    public void sendVoiceMessage(WeixinSession session, Voice content, String... toUsers) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendVoiceMessage(content, toUsers);
    }

    @Override
    public void sendVoiceMessage(WeixinSession session, Voice content, long toGroup) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendVoiceMessage(content, toGroup);
    }

    @Override
    public void sendVideoMessage(WeixinSession session, Video content, String... toUsers) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendVideoMessage(content, toUsers);
    }

    @Override
    public void sendVideoMessage(WeixinSession session, Video content, long toGroup) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendVideoMessage(content, toGroup);
    }

    @Override
    public void sendMusicMessage(WeixinSession session, Music content, String toUser) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendMusicMessage(content, toUser);
    }

    @Override
    public void sendNewsMessage(WeixinSession session, List<News> content, String toUser) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendNewsMessage(content, toUser);
    }

    public void sendNewsMessage(WeixinSession session, List<Article> articles, String... toUsers) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendNewsMessage(articles, toUsers);
    }

    @Override
    public void sendNewsMessage(WeixinSession session, List<Article> articles, long toGroup) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendNewsMessage(articles, toGroup);
    }

    @Override
    public String oauth2buildAuthorizationUrl(WeixinSession session, String redirectUri, Scope scope, String state) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().oauth2buildAuthorizationUrl(redirectUri, scope, state);
    }

    public User getOauth2User(WeixinSession session, String code) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getOauth2User(code);
    }

    @Override
    public void sendTextMessage(WeixinSession session, String content, String... toUsers) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendTextMessage(content, toUsers);
    }

    @Override
    public void sendTextMessage(WeixinSession session, String content, long toGroup) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendTextMessage(content, toGroup);
    }

    @Override
    public void sendTemplateMessage(WeixinSession session, Template content, String toUser) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().sendTemplateMessage(content, toUser);
    }



    @Override
    public List<User> getUsers(WeixinSession session) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getUsers();
    }

    @Override
    public OpenIdList getOpenIds(WeixinSession session) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getOpenIds();
    }

    @Override
    public OpenIdList getOpenIds(WeixinSession session, String nextOpenId) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getOpenIds(nextOpenId);
    }

    @Override
    public User getUser(WeixinSession session, String userId) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getUser(userId);
    }

    @Override
    public String mediaUpload(WeixinSession session, Media.Type mediaType, Object fileItem) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().mediaUpload(mediaType, fileItem);
    }

    public Object mediaDownload(WeixinSession session, String mediaId) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().mediaDownload(mediaId);
    }

    @Override
    public void refreshMenu(WeixinSession session, Menu... menus) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().refreshMenu(menus);
    }

    public Jsapi getJsapi(WeixinSession session) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getJsapi();
    }

    @Override
    public List<Menu> getMenus(WeixinSession session) throws WeixinException {
        return getWeiXinDetails(session.getId()).getWeiXinService().getMenus();
    }

    @Override
    public void clearMenu(WeixinSession session) throws WeixinException {
        getWeiXinDetails(session.getId()).getWeiXinService().clearMenu();
    }

    private WeixinDetails getWeiXinDetails(String appid) throws WeixinException {
        if (!weiXinDetailsMap.containsKey(appid)) {
            throw new WeixinException("[appid=" + appid + "]未注册！");
        }
        return weiXinDetailsMap.get(appid);
    }

}
