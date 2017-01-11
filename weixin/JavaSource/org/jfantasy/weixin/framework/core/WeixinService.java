package org.jfantasy.weixin.framework.core;


import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.WeixinMessage;
import org.jfantasy.weixin.framework.message.content.*;
import org.jfantasy.weixin.framework.message.user.OpenIdList;
import org.jfantasy.weixin.framework.message.user.User;
import org.jfantasy.weixin.framework.oauth2.Scope;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface WeixinService {

    String getJsapiTicket() throws WeixinException;

    String getJsapiTicket(boolean forceRefresh) throws WeixinException;

    Jsapi.Signature createJsapiSignature(String url) throws WeixinException;

    WeixinMessage parseInMessage(HttpServletRequest request) throws WeixinException;

    String parseInMessage(String encryptType, WeixinMessage message) throws WeixinException;

    void sendImageMessage(Image content, String... toUsers) throws WeixinException;

    void sendImageMessage(Image content, long toGroup) throws WeixinException;

    String mediaUpload(Media.Type mediaType, Object fileItem) throws WeixinException;

    void sendVoiceMessage(Voice content, String... toUsers) throws WeixinException;

    void sendVoiceMessage(Voice content, long toGroup) throws WeixinException;

    void sendVideoMessage(Video content, String... toUsers) throws WeixinException;

    void sendVideoMessage(Video content, long toGroup) throws WeixinException;

    void sendMusicMessage(Music content, String toUser) throws WeixinException;

    void sendNewsMessage(List<News> content, String toUser) throws WeixinException;

    void sendNewsMessage(List<Article> articles, String... toUsers) throws WeixinException;

    void sendNewsMessage(List<Article> articles, long toGroup) throws WeixinException;

    void sendTemplateMessage(Template content, String toUser) throws WeixinException;

    void sendTextMessage(String content, String... toUsers) throws WeixinException;

    void sendTextMessage(String content, long toGroup) throws WeixinException;

    String oauth2buildAuthorizationUrl(String redirectUri, Scope scope, String state) throws WeixinException;

    User getOauth2User(String code) throws WeixinException;

    List<User> getUsers() throws WeixinException;

    OpenIdList getOpenIds() throws WeixinException;

    OpenIdList getOpenIds(String nextOpenId) throws WeixinException;

    User getUser(String userId) throws WeixinException;

    @Deprecated
    Object mediaDownload(String mediaId) throws WeixinException;

    void refreshMenu(Menu... menus) throws WeixinException;

    Jsapi getJsapi() throws WeixinException;

    List<Menu> getMenus() throws WeixinException;

    void clearMenu() throws WeixinException;

}
