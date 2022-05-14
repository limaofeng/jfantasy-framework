package org.jfantasy.framework.search.config;

import java.lang.annotation.*;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(IndexedScanPackages.Registrar.class)
public @interface IndexedScan {
  @AliasFor("basePackages")
  String[] value() default {};

  @AliasFor("value")
  String[] basePackages() default {};

  Class<?>[] basePackageClasses() default {};
}
