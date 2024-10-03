/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import net.asany.jfantasy.framework.web.filter.wrapper.CharacterEncodingRequestWrapper;
import org.jetbrains.annotations.NotNull;
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
      HttpServletRequest request,
      @NotNull HttpServletResponse response,
      @NotNull FilterChain filterChain)
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
