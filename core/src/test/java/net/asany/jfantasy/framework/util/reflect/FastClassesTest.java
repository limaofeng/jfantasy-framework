package net.asany.jfantasy.framework.util.reflect;

import java.util.ArrayList;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.mybatis.interceptors.MultiDataSourceInterceptor;
import org.junit.jupiter.api.Test;

@Slf4j
public class FastClassesTest {

  @Test
  public void newInstance() throws Exception {
    FastClasses fastClasses = new FastClasses(MultiDataSourceInterceptor.class);
  }

  @Test
  public void testListClass() {
    FastClasses fastClasses = new FastClasses(ArrayList.class);
    log.info("{}", fastClasses.newInstance().getClass());
  }

  @Test
  public void testMapClass() {
    FastClasses fastClasses = new FastClasses(HashMap.class);
    assert fastClasses.newInstance().getClass() == HashMap.class;
    log.info("{}", fastClasses.newInstance().getClass() == HashMap.class);
  }
}
