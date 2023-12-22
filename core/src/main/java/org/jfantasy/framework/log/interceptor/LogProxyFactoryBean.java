package org.jfantasy.framework.log.interceptor;

import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.Pointcut;
import org.springframework.aop.framework.AbstractSingletonProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;

public class LogProxyFactoryBean extends AbstractSingletonProxyFactoryBean {
  private final LogInterceptor logInterceptor = new LogInterceptor();
  @Setter private transient Pointcut pointcut;

  @Override
  protected @NotNull Object createMainInterceptor() {
    this.logInterceptor.afterPropertiesSet();
    if (this.pointcut != null) {
      return new DefaultPointcutAdvisor(this.pointcut, this.logInterceptor);
    } else {
      throw new UnsupportedOperationException();
    }
  }
}
