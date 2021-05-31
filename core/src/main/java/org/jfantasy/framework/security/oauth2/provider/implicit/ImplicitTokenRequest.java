package org.jfantasy.framework.security.oauth2.provider.implicit;

import org.jfantasy.framework.security.oauth2.provider.OAuth2Request;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;

public class ImplicitTokenRequest extends TokenRequest {

    private OAuth2Request oauth2Request;

    public ImplicitTokenRequest(TokenRequest tokenRequest, OAuth2Request oauth2Request) {
        super(tokenRequest.getRequestParameters(), tokenRequest.getClientId(), tokenRequest.getScope(), tokenRequest.getGrantType());
        this.oauth2Request = oauth2Request;
    }

    public OAuth2Request getOAuth2Request() {
        return oauth2Request;
    }

}