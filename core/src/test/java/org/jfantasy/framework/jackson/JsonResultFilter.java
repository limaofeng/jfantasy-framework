package org.jfantasy.framework.jackson;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(JsonResultFilters.class)
public @interface JsonResultFilter {

  Class<?> type();

  String include() default "";

  String filter() default "";
}
