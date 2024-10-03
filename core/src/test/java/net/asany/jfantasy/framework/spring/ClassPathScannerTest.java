/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.spring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.BaseBusEntity;
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
    Set<String> classeNames =
        pathScanner.findTargetClassNames("net.asany.jfantasy.framework.spring");
    for (String clazz : classeNames) {
      log.debug(clazz);
    }
  }

  @Test
  public void testFindAnnotationedClasses() throws Exception {
    Set<Class<?>> classes = pathScanner.findAnnotationedClasses("", JsonIgnoreProperties.class);
    for (Class<?> clazz : classes) {
      log.debug(clazz.getName());
    }
  }

  @Test
  public void testFindInterfaceClasses() throws Exception {
    Set<Class<?>> classes =
        ClassPathScanner.getInstance()
            .findInterfaceClasses("net.asany.jfantasy.*.bean", BaseBusEntity.class);
    for (Class<?> clazz : classes) {
      log.debug(clazz.getName());
    }
  }
}
