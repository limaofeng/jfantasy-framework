package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.TokenResolver;

/**
 * BearerToken 解析器
 *
 * @author limaofeng
 */
public interface BearerTokenResolver<T> extends TokenResolver<T> {

  @Override
  default AuthType getAuthType() {
    return AuthType.BEARER;
  }
}
