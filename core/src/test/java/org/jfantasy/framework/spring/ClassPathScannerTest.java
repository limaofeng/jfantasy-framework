package org.jfantasy.framework.spring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.BaseBusEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ClassPathScannerTest {

  private ClassPathScanner pathScanner;

  @BeforeEach
  public void setUp() throws Exception {
    pathScanner = new ClassPathScanner();
  }

  @AfterEach
  public void tearDown() throws Exception {}

  @Test
  public void testFindTargetClassNames() throws Exception {
    Set<String> classeNames = pathScanner.findTargetClassNames("org.jfantasy.framework.spring");
    for (String clazz : classeNames) {
      log.debug(clazz);
    }
  }

  @Test
  public void testFindAnnotationedClasses() throws Exception {
    Set<Class> classes = pathScanner.findAnnotationedClasses("", JsonIgnoreProperties.class);
    for (Class clazz : classes) {
      log.debug(clazz.getName());
    }
  }

  @Test
  public void testFindInterfaceClasses() throws Exception {
    Set<Class> classes =
        ClassPathScanner.getInstance()
            .findInterfaceClasses("org.jfantasy.*.bean", BaseBusEntity.class);
    for (Class clazz : classes) {
      log.debug(clazz.getName());
    }
  }
}
