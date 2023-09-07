package org.jfantasy.framework.dao.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;
import org.jfantasy.framework.dao.hibernate.generator.CustomTableGenerator;

@IdGeneratorType(CustomTableGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface TableGenerator {
  String name() default "";

  int initialValue() default 0;

  int allocationSize() default 50;

  int incrementSize() default 1;
}
