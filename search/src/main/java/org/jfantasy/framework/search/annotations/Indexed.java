package org.jfantasy.framework.search.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.jfantasy.framework.search.dao.DataFetcher;
import org.jfantasy.framework.search.dao.jpa.JpaDefaultDataFetcher;

@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Indexed {
  Class<? extends DataFetcher> fetcher() default JpaDefaultDataFetcher.class;
}
