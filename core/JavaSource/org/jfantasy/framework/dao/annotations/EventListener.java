package org.jfantasy.framework.dao.annotations;

import org.hibernate.event.spi.EventType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventListener {

    /**
     * EventType
     * {@link EventType}
     * @return String[]
     */
    String[] value() default {"post-commit-insert", "post-commit-update"};

}
