package org.jfantasy.framework.security.oauth2.provider.code;

import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.*;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Request;
import org.jfantasy.framework.security.oauth2.provider.OAuth2RequestFactory;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;
import org.jfantasy.framework.security.oauth2.provider.token.AbstractTokenGranter;
import org.jfantasy.framework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.HashMap;
import java.util.Map;

public class AuthorizationCodeTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "authorization_code";

    private final AuthorizationCodeServices authorizationCodeServices;

    public AuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices,
                                         AuthorizationCodeServices authorizationCodeServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(tokenServices, authorizationCodeServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected AuthorizationCodeTokenGranter(AuthorizationServerTokenServices tokenServices, AuthorizationCodeServices authorizationCodeServices,
                                            ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authorizationCodeServices = authorizationCodeServices;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = tokenRequest.getRequestParameters();
        String authorizationCode = parameters.get("code");
        String redirectUri = parameters.get("REDIRECT_URI");

        if (authorizationCode == null) {
            throw new InvalidRequestException("An authorization code must be supplied.");
        }

        OAuth2Authentication storedAuth = authorizationCodeServices.consumeAuthorizationCode(authorizationCode);
        if (storedAuth == null) {
            throw new InvalidGrantException("Invalid authorization code: " + authorizationCode);
        }

        OAuth2Request pendingOAuth2Request = storedAuth.getOAuth2Request();
        String redirectUriApprovalParameter = pendingOAuth2Request.getRequestParameters().get("REDIRECT_URI");


        if ((redirectUri != null || redirectUriApprovalParameter != null) && !pendingOAuth2Request.getRedirectUri().equals(redirectUri)) {
            throw new RedirectMismatchException("Redirect URI mismatch.");
        }

        String pendingClientId = pendingOAuth2Request.getClientId();
        String clientId = tokenRequest.getClientId();
        if (clientId != null && !clientId.equals(pendingClientId)) {
            throw new InvalidClientException("Client ID mismatch");
        }

        Map<String, String> combinedParameters = new HashMap<String, String>(pendingOAuth2Request.getRequestParameters());
        combinedParameters.putAll(parameters);

        // Make a new stored request with the combined parameters
        OAuth2Request finalStoredOAuth2Request = pendingOAuth2Request.createOAuth2Request(combinedParameters);

        Authentication userAuth = storedAuth.getUserAuthentication();

        return new OAuth2Authentication(finalStoredOAuth2Request, userAuth);

    }

}
