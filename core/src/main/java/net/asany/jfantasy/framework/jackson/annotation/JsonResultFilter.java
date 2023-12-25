package net.asany.jfantasy.framework.jackson.annotation;

import java.lang.annotation.*;

/**
 * @author limaofeng
 */
@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JsonResultFilter {

  BeanFilter[] value();
}
