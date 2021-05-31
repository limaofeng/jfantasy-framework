package org.jfantasy.framework.security.oauth2.provider;

import org.jfantasy.framework.security.oauth2.ClientDetails;

import java.util.Map;

public interface OAuth2RequestFactory {

    AuthorizationRequest createAuthorizationRequest(Map<String, String> authorizationParameters);

    OAuth2Request createOAuth2Request(AuthorizationRequest request);

    OAuth2Request createOAuth2Request(ClientDetails client, TokenRequest tokenRequest);

    TokenRequest createTokenRequest(Map<String, String> requestParameters, ClientDetails authenticatedClient);

    TokenRequest createTokenRequest(AuthorizationRequest authorizationRequest, String grantType);
}
