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
package net.asany.jfantasy.framework.jackson.deserializer;

import static org.junit.jupiter.api.Assertions.*;

import groovy.util.logging.Slf4j;
import java.util.Date;
import net.asany.jfantasy.framework.util.common.BeanUtil;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
class DateDeserializerTest {

  private static final Logger log = LoggerFactory.getLogger(DateDeserializerTest.class);

  @Test
  void convertStringToObject() {
    Date date = BeanUtil.convertStringToObject("2024-04-09T08:53:09.000Z", Date.class);
    log.debug(date.toString());
  }
}
