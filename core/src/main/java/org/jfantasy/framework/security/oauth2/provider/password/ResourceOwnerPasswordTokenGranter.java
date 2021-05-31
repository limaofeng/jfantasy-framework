package org.jfantasy.framework.security.oauth2.provider.password;

import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.authentication.*;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.oauth2.ClientDetails;
import org.jfantasy.framework.security.oauth2.ClientDetailsService;
import org.jfantasy.framework.security.oauth2.InvalidGrantException;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Request;
import org.jfantasy.framework.security.oauth2.provider.OAuth2RequestFactory;
import org.jfantasy.framework.security.oauth2.provider.TokenRequest;
import org.jfantasy.framework.security.oauth2.provider.token.AbstractTokenGranter;
import org.jfantasy.framework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.LinkedHashMap;
import java.util.Map;

public class ResourceOwnerPasswordTokenGranter extends AbstractTokenGranter {
    private static final String GRANT_TYPE = "password";

    private final AuthenticationManager authenticationManager;

    public ResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager,
                                             AuthorizationServerTokenServices tokenServices, ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory) {
        this(authenticationManager, tokenServices, clientDetailsService, requestFactory, GRANT_TYPE);
    }

    protected ResourceOwnerPasswordTokenGranter(AuthenticationManager authenticationManager, AuthorizationServerTokenServices tokenServices,
                                                ClientDetailsService clientDetailsService, OAuth2RequestFactory requestFactory, String grantType) {
        super(tokenServices, clientDetailsService, requestFactory, grantType);
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {

        Map<String, String> parameters = new LinkedHashMap<String, String>(tokenRequest.getRequestParameters());
        String username = parameters.get("username");
        String password = parameters.get("password");
        // Protect from downstream leaks of password
        parameters.remove("password");

        Authentication userAuth = new UsernamePasswordAuthenticationToken(username, password);
        ((AbstractAuthenticationToken) userAuth).setDetails(parameters);
        try {
            userAuth = authenticationManager.authenticate(userAuth);
        } catch (AccountStatusException | BadCredentialsException | UsernameNotFoundException e) {
            throw new InvalidGrantException(e.getMessage());
        }
        if (userAuth == null || !userAuth.isAuthenticated()) {
            throw new InvalidGrantException("Could not authenticate user: " + username);
        }

        OAuth2Request storedOAuth2Request = getRequestFactory().createOAuth2Request(client, tokenRequest);
        return new OAuth2Authentication(storedOAuth2Request, userAuth);
    }
}