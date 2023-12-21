package org.jfantasy.graphql.error;

import org.jetbrains.annotations.NotNull;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.aop.ClassFilter;

/**
 * @author limaofeng
 * @version V1.0
 */
public class GraphQLClassFilter implements ClassFilter {
  private final Class<?>[] classes;

  public GraphQLClassFilter(Class<?>[] classes) {
    this.classes = classes;
  }

  @Override
  public boolean matches(@NotNull Class<?> clazz) {
    return ClassUtil.hasInterface(clazz, this.classes);
  }
}
