package org.jfantasy.framework.security.oauth2.provider;

public interface TokenGranter {

    OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest);

}
