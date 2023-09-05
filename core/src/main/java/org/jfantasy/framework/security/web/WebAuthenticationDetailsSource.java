package org.jfantasy.framework.security.web;

import javax.servlet.http.HttpServletRequest;
import org.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import org.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;

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
