package org.jfantasy.graphql.error;

import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.aop.ClassFilter;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/22 8:17 下午
 */
public class GraphQLClassFilter implements ClassFilter {
  private Class[] classes;

  public GraphQLClassFilter(Class[] classes) {
    this.classes = classes;
  }

  @Override
  public boolean matches(Class<?> clazz) {
    return ClassUtil.hasInterface(clazz, this.classes);
  }
}
