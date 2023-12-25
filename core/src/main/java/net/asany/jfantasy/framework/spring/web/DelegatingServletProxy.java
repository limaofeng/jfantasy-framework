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
