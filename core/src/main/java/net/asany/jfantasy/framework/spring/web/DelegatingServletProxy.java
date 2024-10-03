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
package net.asany.jfantasy.framework.spring.web;

import jakarta.servlet.*;
import java.io.IOException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class DelegatingServletProxy extends GenericServlet {
  private String targetBean;
  private transient Servlet proxy;

  @Override
  public void service(ServletRequest req, ServletResponse res)
      throws ServletException, IOException {
    proxy.service(req, res);
  }

  @Override
  public void init() throws ServletException {
    this.targetBean = getServletName();
    getServletBean();
    proxy.init(getServletConfig());
  }

  private void getServletBean() {
    WebApplicationContext wac =
        WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
    this.proxy = (Servlet) wac.getBean(targetBean);
  }
}
