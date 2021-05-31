package org.jfantasy.framework.security.oauth2.provider.implicit;

import org.jfantasy.framework.security.SecurityContextHolder;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.oauth2.ClientDetails;
import org.jfantasy.framework.security.oauth2.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.InsufficientAuthenticationException;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Request;
import org.jfantasy.framework.security.oauth2.provider.OAuth2RequestFactory;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;
import org.jfantasy.framework.security.oauth2.provider.token.AbstractTokenGranter;
import org.jfantasy.framework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.Assert;

public class ImplicitTokenGranter extends AbstractTokenGranter {

    private static final String GRANT_TYPE = "implicit";

    public ImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected ImplicitTokenGranter(AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService,
                                   OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest clientToken) {

        Authentication userAuth = SecurityContextHolder.getContext().getAuthentication();
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InsufficientAuthenticationException("There is no currently logged in user");
        }
        Assert.state(clientToken instanceof ImplicitTokenRequest, "An ImplicitTokenRequest is required here. Caller needs to wrap the TokenRequest.");

        OAuth2Request requestForStorage = ((ImplicitTokenRequest) clientToken).getOAuth2Request();

        return new OAuth2Authentication(requestForStorage, userAuth);

    }


}
