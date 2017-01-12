package org.jfantasy.weixin.framework.core;

import org.jfantasy.weixin.framework.exception.WeixinException;
import org.jfantasy.weixin.framework.message.user.User;
import org.jfantasy.weixin.framework.oauth2.AccessToken;
import org.jfantasy.weixin.framework.oauth2.Scope;

public interface Openapi {

    User getUser(String code) throws WeixinException;

    User getUser(AccessToken token) throws WeixinException;

    AccessToken getAccessToken(String code) throws WeixinException;

    String getAuthorizationUrl(String redirectUri, Scope scope, String state) throws WeixinException;

}
