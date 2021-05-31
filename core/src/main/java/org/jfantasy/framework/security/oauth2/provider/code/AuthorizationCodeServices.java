package org.jfantasy.framework.security.oauth2.provider.code;

import org.jfantasy.framework.security.oauth2.InvalidGrantException;
import org.jfantasy.framework.security.oauth2.provider.OAuth2Authentication;

public interface AuthorizationCodeServices {
    String createAuthorizationCode(OAuth2Authentication authentication);

    OAuth2Authentication consumeAuthorizationCode(String code)
        throws InvalidGrantException;

}
