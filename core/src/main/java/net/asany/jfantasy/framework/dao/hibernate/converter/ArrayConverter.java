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
import java.lang.reflect.Array;
import net.asany.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

/**
 * 数组转换器
 *
 * @author limaofeng
 */
public class ArrayConverter<T> implements AttributeConverter<T[], String> {

  protected Class<?> entityClass;

  public ArrayConverter() {
    this.entityClass =
        ReflectionUtils.getSuperClassGenricType(ClassUtil.getRealClass(getClass()))
            .getComponentType();
  }

  @Override
  public String convertToDatabaseColumn(T[] attribute) {
    if (attribute == null) {
      return null;
    }
    return JSON.serialize(attribute);
  }

  @Override
  public T[] convertToEntityAttribute(String dbData) {
    if (StringUtil.isBlank(dbData)) {
      return null;
    }
    //noinspection unchecked
    return (T[]) JSON.deserialize(dbData, Array.newInstance(entityClass, 0).getClass());
  }
}
