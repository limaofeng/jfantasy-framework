package org.jfantasy.framework.web.tomcat;

import org.apache.catalina.Context;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;

/**
 * 自定义TomcatContextCustomizer
 *
 * @author limaofeng
 */
public class CustomTomcatContextCustomizer implements TomcatContextCustomizer {
  @Override
  public void customize(Context context) {
    context.setManager(new NoSessionManager());
  }
}
