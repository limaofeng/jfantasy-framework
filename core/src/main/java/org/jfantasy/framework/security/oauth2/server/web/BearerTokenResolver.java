package org.jfantasy.framework.security.oauth2.server.web;

import jakarta.servlet.http.HttpServletRequest;

/**
 * BearerToken 解析器
 *
 * @author limaofeng
 */
public interface BearerTokenResolver {
  String resolve(HttpServletRequest request);
}
