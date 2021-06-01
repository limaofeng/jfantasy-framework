package org.jfantasy.framework.security.oauth2;

/**
 * OAuth2 认证详情
 *
 * @author limaofeng
 */
public class OAuth2AuthenticationDetails {

    private String clientId;

    public OAuth2AuthenticationDetails(String clientId) {
        this.clientId = clientId;
    }

    public String getClientId() {
        return this.clientId;
    }
}
