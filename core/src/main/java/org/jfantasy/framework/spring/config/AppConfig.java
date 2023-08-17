package org.jfantasy.framework.spring.config;

import java.util.concurrent.Executor;
import org.jfantasy.schedule.service.TaskScheduler;
import org.quartz.Scheduler;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 应用配置类 <br>
 *
 * <p>负责注册除Controller等web层以外的所有bean，包括aop代理，service层，dao层，缓存，等等
 *
 * @author limaofeng
 */
@Configuration
@EnableAsync
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AppConfig {

  @Bean
  public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }

  @Bean
  public TaskScheduler taskScheduler(@Autowired(required = false) Scheduler scheduler) {
    return new TaskScheduler(scheduler);
  }

  @Configuration
  public static class ThreadingConfig implements AsyncConfigurer {

    @Override
    public Executor getAsyncExecutor() {
      ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
      pool.setMaxPoolSize(10);
      pool.setCorePoolSize(10);
      pool.setThreadNamePrefix("Spring-Async-");
      pool.initialize();
      return pool;
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
      return new SimpleAsyncUncaughtExceptionHandler();
    }
  }
}
