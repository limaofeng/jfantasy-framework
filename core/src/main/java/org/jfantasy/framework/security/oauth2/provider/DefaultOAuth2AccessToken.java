package org.jfantasy.framework.security.oauth2.provider;

import lombok.Data;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Data
public class DefaultOAuth2AccessToken implements OAuth2AccessToken {
    private String value;

    private Date expiration;

    private String tokenType = "bearer";

    private OAuth2RefreshToken refreshToken;

    private Set<String> scope;

    private Map<String, Object> additionalInformation = Collections.emptyMap();

    public DefaultOAuth2AccessToken(String value) {
        this.value = value;
    }

    public DefaultOAuth2AccessToken(OAuth2AccessToken accessToken) {
        this(accessToken.getValue());
        setAdditionalInformation(accessToken.getAdditionalInformation());
        setRefreshToken(accessToken.getRefreshToken());
        setExpiration(accessToken.getExpiration());
        setScope(accessToken.getScope());
        setTokenType(accessToken.getTokenType());
    }

    @Override
    public int getExpiresIn() {
        return 0;
    }

    @Override
    public boolean isExpired() {
        return false;
    }
}
