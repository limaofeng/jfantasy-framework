package org.jfantasy.framework.security.oauth2.server.authentication;

import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.oauth2.core.OAuth2AccessToken;
import org.jfantasy.framework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class BearerTokenAuthentication extends AbstractOAuth2TokenAuthenticationToken<OAuth2AccessToken> {

    private final Map<String, Object> attributes;

    public BearerTokenAuthentication(OAuth2AuthenticatedPrincipal principal, OAuth2AccessToken credentials, Collection<? extends GrantedAuthority> authorities) {
        super(credentials, principal, credentials, authorities);
        Assert.isTrue(credentials.getTokenType() == OAuth2AccessToken.TokenType.BEARER, "credentials must be a bearer token");
        this.attributes = Collections.unmodifiableMap(new LinkedHashMap<>(principal.getAttributes()));
        setAuthenticated(true);
    }

    @Override
    public Map<String, Object> getTokenAttributes() {
        return this.attributes;
    }

}
