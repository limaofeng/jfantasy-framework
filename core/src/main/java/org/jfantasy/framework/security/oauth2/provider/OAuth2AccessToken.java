package org.jfantasy.framework.security.oauth2.provider;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public interface OAuth2AccessToken {

    Map<String, Object> getAdditionalInformation();

    Date getExpiration();

    int getExpiresIn();

    OAuth2RefreshToken getRefreshToken();

    Set<String> getScope();

    String getTokenType();

    String getValue();

    boolean isExpired();

}
