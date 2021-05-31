package org.jfantasy.framework.security.oauth2.provider.refresh;

import org.jfantasy.framework.security.authentication.AccountStatusException;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.oauth2.ClientDetails;
import org.jfantasy.framework.security.oauth2.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.InvalidGrantException;
import org.jfantasy.framework.security.oauth2.provider.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2RequestFactory;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;
import org.jfantasy.framework.security.oauth2.provider.token.AbstractTokenGranter;
import org.jfantasy.framework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class RefreshTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "refresh_token";

    public RefreshTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected RefreshTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                  OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        String refreshToken = tokenRequest.getRequestParameters().get("refresh_token");
        try {
            return getTokenServices().refreshAccessToken(refreshToken, tokenRequest);
        } catch (AccountStatusException | UsernameNotFoundException e) {
            throw new InvalidGrantException(e.getMessage());
        }
    }
}