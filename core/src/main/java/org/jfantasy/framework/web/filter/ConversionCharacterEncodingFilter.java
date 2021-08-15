package org.jfantasy.framework.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jfantasy.framework.web.filter.wrapper.CharacterEncodingRequestWrapper;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 终极乱码解决方法
 *
 * @author limaofeng
 */
public class ConversionCharacterEncodingFilter extends OncePerRequestFilter {

  private static final String TRANSFORM = "ConversionCharacterEncodingFilter.transform";

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    Boolean transform = (Boolean) request.getAttribute(TRANSFORM);
    if (transform == null) {
      filterChain.doFilter(new CharacterEncodingRequestWrapper(request), response);
      request.setAttribute(TRANSFORM, Boolean.TRUE);
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
