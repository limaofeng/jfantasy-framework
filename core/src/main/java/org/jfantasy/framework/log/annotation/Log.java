package org.jfantasy.framework.log.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jfantasy.framework.log.filter.DefaultLogFilter;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Log {
  /**
   * 简要描述信息
   *
   * @return String
   */
  String text() default "";

  /**
   * 判断是否记录日志
   *
   * @return String
   */
  String condition() default "";

  /**
   * 日志连接器类型
   *
   * @return String
   */
  String type() default "simple";

  /**
   * 日志拦截器
   *
   * @return Class<?>
   */
  Class<?> using() default DefaultLogFilter.class;
}
