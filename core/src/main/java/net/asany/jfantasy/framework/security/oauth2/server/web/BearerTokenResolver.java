package net.asany.jfantasy.framework.security.oauth2.server.web;

/**
 * BearerToken 解析器
 *
 * @author limaofeng
 */
public interface BearerTokenResolver<T> {

  /**
   * 解析
   *
   * @param request T
   * @return token
   */
  String resolve(T request);
}
