package org.jfantasy.framework.security.oauth2.server.web;

import javax.servlet.http.HttpServletRequest;

public interface BearerTokenResolver {
    String resolve(HttpServletRequest request);
}
