package org.jfantasy.framework.security.web;

import org.jfantasy.framework.security.authentication.AuthenticationDetailsSource;
import org.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * @author limaofeng
 */
public class WebAuthenticationDetailsSource implements AuthenticationDetailsSource<HttpServletRequest, WebAuthenticationDetails> {

    @Override
    public WebAuthenticationDetails buildDetails(HttpServletRequest context) {
        return new WebAuthenticationDetails(context);
    }

}
