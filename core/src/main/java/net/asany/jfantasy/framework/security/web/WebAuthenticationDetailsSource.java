package net.asany.jfantasy.framework.security.web;

import jakarta.servlet.http.HttpServletRequest;
import net.asany.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import net.asany.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;

/**
 * WebAuthenticationDetailsSource
 *
 * @author limaofeng
 */
public class WebAuthenticationDetailsSource
    implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

  @Override
  public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
    return new WebAuthenticationDetails(context);
  }
}
