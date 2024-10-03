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

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.crypto.CryptoException;
import net.asany.jfantasy.framework.crypto.DESPlus;

/**
 * DES加密转换器
 *
 * @author limaofeng
 */
@Slf4j
public class DESConverter implements AttributeConverter<String, String> {

  private static DESPlus desPlus;

  public DESConverter() {
    desPlus = new DESPlus();
  }

  public DESConverter(String key) {
    desPlus = new DESPlus(key);
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    try {
      return desPlus.encrypt(attribute);
    } catch (CryptoException e) {
      log.debug(e.getMessage(), e);
      return attribute;
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    try {
      return desPlus.decrypt(dbData);
    } catch (CryptoException e) {
      log.debug(e.getMessage(), e);
      return dbData;
    }
  }
}
