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
