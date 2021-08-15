package org.jfantasy.framework.dao.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 多数据源情况下，用于标示对应的数据源
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:06:32
 */
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataSource {

  String name();

  String catalog() default "";
}
