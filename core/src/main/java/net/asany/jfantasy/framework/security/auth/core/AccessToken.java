package net.asany.jfantasy.framework.security.auth.core;

public interface AccessToken extends AuthToken {

  String getRefreshTokenValue();
}
