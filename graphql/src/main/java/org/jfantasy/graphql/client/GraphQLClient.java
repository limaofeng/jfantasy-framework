package org.jfantasy.graphql.client;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/** @author limaofeng */
@Target(java.lang.annotation.ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface GraphQLClient {
  /**
   * 对应配置
   *
   * @return
   */
  String value() default "GLOBAL";
}
