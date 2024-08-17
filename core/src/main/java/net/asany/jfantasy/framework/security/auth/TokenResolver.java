package net.asany.jfantasy.framework.security.auth;

/**
 * Token 解析器
 *
 * @param <T>
 */
public interface TokenResolver<T> {

  boolean supports(T request);

  String resolve(T request);

  AuthType getAuthType();

  AuthTokenType getAuthTokenType();
}
