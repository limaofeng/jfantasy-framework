package org.jfantasy.framework.security.oauth2.provider.token;

import org.jfantasy.framework.security.oauth2.ClientDetails;
import org.jfantasy.framework.security.oauth2.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.InvalidClientException;
import org.jfantasy.framework.security.oauth2.provider.*;

import java.util.Collection;

public abstract class AbstractTokenGranter implements TokenGranter {

    private final AuthorizationServerTokenServices tokenServices;

    private final ClientDetailsService clientDetailsService;

    private final OAuth2RequestFactory requestFactory;

    private final String grantType;

    protected AbstractTokenGranter(AuthorizationServerTokenServices tokenServices,
                                   ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        this.clientDetailsService = clientDetailsService;
        this.grantType = grantType;
        this.tokenServices = tokenServices;
        this.requestFactory = requestFactory;
    }

    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {

        if (!this.grantType.equals(grantType)) {
            return null;
        }

        String clientId = tokenRequest.getClientId();
        ClientDetails client = clientDetailsService.loadClientByClientId(clientId);
        validateGrantType(grantType, client);

        return getAccessToken(client, tokenRequest);

    }

    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        return tokenServices.createAccessToken(getOAuth2Authentication(client, tokenRequest));
    }

    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2Request storedOAuth2Request = requestFactory.createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, null);
    }

    protected void validateGrantType(String grantType, ClientDetails clientDetails) {
        Collection<String> authorizedGrantTypes = clientDetails.getAuthorizedGrantTypes();
        if (authorizedGrantTypes != null && !authorizedGrantTypes.isEmpty()
            && !authorizedGrantTypes.contains(grantType)) {
            throw new InvalidClientException("Unauthorized grant type: " + grantType);
        }
    }

    protected AuthorizationServerTokenServices getTokenServices() {
        return tokenServices;
    }

    protected OAuth2RequestFactory getRequestFactory() {
        return requestFactory;
    }

}
