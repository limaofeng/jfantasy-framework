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
package net.asany.jfantasy.framework.search.elastic;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import net.asany.jfantasy.framework.search.annotations.IndexProperty;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.reflect.Property;

public class ElasticPropertyNamingStrategy extends PropertyNamingStrategy {
  @Override
  public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
    return super.nameForField(config, field, defaultName);
  }

  @Override
  public String nameForGetterMethod(
      MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    Property property = ClassUtil.getProperty(method.getDeclaringClass(), defaultName);
    IndexProperty indexProperty = property.getAnnotation(IndexProperty.class);
    if (indexProperty == null || StringUtil.isBlank(indexProperty.name())) {
      return super.nameForSetterMethod(config, method, defaultName);
    }
    return indexProperty.name();
  }

  @Override
  public String nameForSetterMethod(
      MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    Property property = ClassUtil.getProperty(method.getDeclaringClass(), defaultName);
    IndexProperty indexProperty = property.getAnnotation(IndexProperty.class);
    if (indexProperty == null || StringUtil.isBlank(indexProperty.name())) {
      return super.nameForSetterMethod(config, method, defaultName);
    }
    return indexProperty.name();
  }

  @Override
  public String nameForConstructorParameter(
      MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
    return super.nameForConstructorParameter(config, ctorParam, defaultName);
  }
}
