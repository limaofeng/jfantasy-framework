package org.jfantasy.framework.security.oauth2.provider.token;

public interface ConsumerTokenServices {
    boolean revokeToken(String tokenValue);
}
