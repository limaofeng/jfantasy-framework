package org.jfantasy.framework.dao.datasource;

import java.lang.reflect.Method;
import javax.sql.DataSource;
import net.ttddyy.dsproxy.listener.logging.SLF4JLogLevel;
import net.ttddyy.dsproxy.support.ProxyDataSource;
import net.ttddyy.dsproxy.support.ProxyDataSourceBuilder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ReflectionUtils;

/**
 * 数据源代理
 *
 * @author limaofeng
 */
public class DatasourceProxyBeanPostProcessor implements BeanPostProcessor {

  @Override
  public Object postProcessAfterInitialization(@NotNull Object bean, @NotNull String beanName) {
    if (bean instanceof DataSource && !(bean instanceof ProxyDataSource)) {
      final ProxyFactory factory = new ProxyFactory(bean);
      factory.setProxyTargetClass(true);
      factory.addAdvice(new ProxyDataSourceInterceptor((DataSource) bean));
      return factory.getProxy();
    }
    return bean;
  }

  @Override
  public Object postProcessBeforeInitialization(@NotNull Object bean, @NotNull String beanName) {
    return bean;
  }

  private record ProxyDataSourceInterceptor(DataSource dataSource) implements MethodInterceptor {
    private ProxyDataSourceInterceptor(final DataSource dataSource) {
      String name = ObjectUtil.getValue("poolName", dataSource);
      this.dataSource =
          ProxyDataSourceBuilder.create(dataSource)
              .name(name + "_proxy")
              .multiline()
              .logQueryBySlf4j(SLF4JLogLevel.INFO)
              .build();
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
      final Method proxyMethod =
          ReflectionUtils.findMethod(this.dataSource.getClass(), invocation.getMethod().getName());
      if (proxyMethod != null) {
        return proxyMethod.invoke(this.dataSource, invocation.getArguments());
      }
      return invocation.proceed();
    }
  }
}
