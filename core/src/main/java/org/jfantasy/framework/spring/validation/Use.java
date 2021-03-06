package org.jfantasy.framework.spring.validation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/** @author limaofeng */
@Constraint(validatedBy = UseConstraintValidator.class)
@Target({METHOD, FIELD})
@Retention(RUNTIME)
@Documented
public @interface Use {

  String message() default "";

  Class<? extends Validator<?>> value();

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
