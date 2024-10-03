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
package net.asany.jfantasy.framework.dao.hibernate.converter;

import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class DESConverterTest {

  private final DESConverter converter = new DESConverter("hooluesoft");

  @Test
  public void testConvertToDatabaseColumn() {
    log.debug(converter.convertToDatabaseColumn("中文"));

    log.debug(converter.convertToDatabaseColumn("李茂峰"));

    log.debug(converter.convertToDatabaseColumn("15921884771"));

    log.debug(
        new DESConverter()
            .convertToDatabaseColumn(new String(new byte[] {63, 63, 63}, StandardCharsets.UTF_8)));
    log.debug(new DESConverter().convertToDatabaseColumn("李茂峰"));
  }

  @Test
  public void testConvertToEntityAttribute() {
    log.debug(converter.convertToEntityAttribute("d699ef0e932711e6"));

    log.debug(converter.convertToEntityAttribute("11196231db5fc99eff08925a14c3dae5"));

    log.debug(converter.convertToEntityAttribute("4ae53296a74f57280e2c7067d5a53bba"));

    log.debug(new DESConverter().convertToEntityAttribute("fefd3fca92327867"));
  }
}
