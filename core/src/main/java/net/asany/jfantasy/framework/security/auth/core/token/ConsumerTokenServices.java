package net.asany.jfantasy.framework.security.auth.core.token;

public interface ConsumerTokenServices {
  boolean revokeToken(String tokenValue);
}
