package org.jfantasy.framework.dao.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;
import org.jfantasy.framework.dao.hibernate.generator.DefaultSerialNumberGenerator;

@IdGeneratorType(DefaultSerialNumberGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface SerialNumberGenerator {
  String value() default "";
}
