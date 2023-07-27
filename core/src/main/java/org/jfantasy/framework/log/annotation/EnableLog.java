package org.jfantasy.framework.log.annotation;

import static org.springframework.context.annotation.AdviceMode.PROXY;

import java.lang.annotation.*;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;

/**
 * 启用日志
 *
 * @author limaofeng
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogConfigurationSelector.class)
public @interface EnableLog {

  /**
   * 代理目标类
   *
   * @return bool
   */
  boolean proxyTargetClass() default false;

  /**
   * 建议模式
   *
   * @return AdviceMode
   */
  AdviceMode mode() default PROXY;

  /**
   * 排序
   *
   * @return int
   */
  int order() default Ordered.LOWEST_PRECEDENCE;
}
