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
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import net.asany.jfantasy.framework.web.filter.wrapper.XSSRequestWrapper;
import org.springframework.web.filter.GenericFilterBean;

public class XSSFilter extends GenericFilterBean {

  /** 是否转编码GET请求的参数 8859_1 => request.getCharacterEncoding() */
  private boolean transform = false;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request, this.transform), response);
  }

  public void setTransform(boolean transform) {
    this.transform = transform;
  }
}
