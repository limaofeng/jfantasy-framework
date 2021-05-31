package org.jfantasy.framework.security.oauth2.provider.client;

import org.jfantasy.framework.security.oauth2.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.provider.DefaultOAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.provider.OAuth2RequestFactory;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;
import org.jfantasy.framework.security.oauth2.provider.token.AbstractTokenGranter;
import org.jfantasy.framework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class ClientCredentialsTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "client_credentials";
    private boolean allowRefresh = false;

    public ClientCredentialsTokenGranter(AuthorizationServerTokenServices tokenServices,
                                         ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected ClientCredentialsTokenGranter(AuthorizationServerTokenServices tokenServices,
                                            ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    public void setAllowRefresh(boolean allowRefresh) {
        this.allowRefresh = allowRefresh;
    }

    @Override
    public OAuth2AccessToken grant(String grantType, TokenRequest tokenRequest) {
        OAuth2AccessToken token = super.grant(grantType, tokenRequest);
        if (token != null) {
            DefaultOAuth2AccessToken norefresh = new DefaultOAuth2AccessToken(token);
            // The spec says that client credentials should not be allowed to get a refresh token
            if (!allowRefresh) {
                norefresh.setRefreshToken(null);
            }
            token = norefresh;
        }
        return token;
    }

}
