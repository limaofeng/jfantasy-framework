package org.jfantasy.framework.util.reflect;

import org.jfantasy.framework.dao.mybatis.interceptors.MultiDataSourceInterceptor;
import org.junit.jupiter.api.Test;

public class FastClassesTest {

  @Test
  public void newInstance() throws Exception {
    FastClasses fastClasses = new FastClasses(MultiDataSourceInterceptor.class);
  }
}
