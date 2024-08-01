package net.asany.jfantasy.framework.dao.hibernate.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import net.asany.jfantasy.framework.dao.hibernate.generator.TableIdentifierGenerator;
import org.hibernate.annotations.IdGeneratorType;

@IdGeneratorType(TableIdentifierGenerator.class)
@Retention(RUNTIME)
@Target({METHOD, FIELD})
public @interface TableGenerator {
  String name() default "";

  int initialValue() default 0;

  int allocationSize() default 50;

  int incrementSize() default 1;
}
