package org.jfantasy.framework.security.oauth2.core.token;

public interface ConsumerTokenServices {
    boolean revokeToken(String tokenValue);
}
