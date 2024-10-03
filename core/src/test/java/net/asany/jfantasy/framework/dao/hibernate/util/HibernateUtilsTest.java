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
package net.asany.jfantasy.framework.dao.hibernate.util;

import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.mybatis.keygen.bean.Sequence;
import org.junit.jupiter.api.Test;

@Slf4j
class HibernateUtilsTest {

  @Test
  public void getEntityName() throws Exception {
    String name = HibernateUtils.getEntityName(Sequence.class);
    log.debug(name);
  }

  @Test
  public void getTableName() throws Exception {
    String name = HibernateUtils.getTableName(Sequence.class);
    log.debug(name);
  }

  @Test
  void getIdName() {
    String name = HibernateUtils.getIdName(Sequence.class);
    log.debug(name);
  }

  @Test
  void getIdValue() {
    Sequence sequence = new Sequence();
    sequence.setKey("1123");
    String value = HibernateUtils.getIdValue(Sequence.class, sequence);
    log.debug(value);
  }
}
