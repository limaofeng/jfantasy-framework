package net.asany.jfantasy.framework.dao.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import net.asany.jfantasy.framework.dao.hibernate.generator.SerialNumberIdentifierGenerator;
import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(SerialNumberIdentifierGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface SerialNumberGenerator {
  String value() default "";
}
