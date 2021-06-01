package org.jfantasy.framework.security.oauth2;

import org.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * OAuth2 认证详情
 *
 * @author limaofeng
 */
public class OAuth2AuthenticationDetails extends WebAuthenticationDetails {

    private String clientId;

    public OAuth2AuthenticationDetails(HttpServletRequest request, String clientId) {
        super(request);
        this.clientId = clientId;
    }

    public String getClientId() {
        return this.clientId;
    }
}
