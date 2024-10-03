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
package net.asany.jfantasy.framework.dao.hibernate.spi;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.asany.jfantasy.framework.dao.hibernate.generator.TableIdentifierGenerator;
import net.asany.jfantasy.framework.dao.hibernate.util.TypeFactory;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.ognl.OgnlUtil;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.spi.EventSource;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.IdentifierGeneratorFactory;

public class IdentifierGeneratorUtil {

  private static final ConcurrentMap<Class<?>, Map<String, IdentifierGenerator>> generatorCache =
      new ConcurrentHashMap<>();

  public static boolean contains(Class<?> entityClass) {
    if (generatorCache.containsKey(entityClass)) {
      return true;
    }
    generatorCache.put(entityClass, new HashMap<>());
    return false;
  }

  public static Set<Map.Entry<String, IdentifierGenerator>> getIdentifierGenerators(
      Class<?> entityClass) {
    return generatorCache.get(entityClass).entrySet();
  }

  public static void initialize(
      EntityState entityState,
      EventSource eventSource,
      Object object,
      IdentifierGeneratorFactory identifierGeneratorFactory) {
    if (entityState == EntityState.TRANSIENT) {
      Class<?> entityClass = object.getClass();
      // 获取实体中,需要自动生成的字段
      loadEntityClass(identifierGeneratorFactory, entityClass);
      // 调用IdentifierGenerator生成
      for (Map.Entry<String, IdentifierGenerator> entry :
          IdentifierGeneratorUtil.getIdentifierGenerators(entityClass)) {
        OgnlUtil.getInstance()
            .setValue(entry.getKey(), object, entry.getValue().generate(eventSource, object));
      }
    }
  }

  private static void loadEntityClass(
      IdentifierGeneratorFactory identifierGeneratorFactory, Class<?> entityClass) {
    if (IdentifierGeneratorUtil.contains(entityClass)) {
      return;
    }
    Field[] fields = ClassUtil.getDeclaredFields(entityClass, GenericGenerator.class);
    for (Field field : fields) {
      if (!field.isAnnotationPresent(Id.class)) {
        GenericGenerator annotGenerator = field.getAnnotation(GenericGenerator.class);
        Properties properties = new Properties();
        properties.put(
            TableIdentifierGenerator.KEY_NAME,
            entityClass.getAnnotation(Table.class).name()
                + ":"
                + field.getAnnotation(Column.class).name());
        for (Parameter parameter : annotGenerator.parameters()) {
          properties.put(parameter.name(), parameter.value());
          IdentifierGenerator generator =
              (IdentifierGenerator)
                  identifierGeneratorFactory.createIdentifierGenerator(
                      annotGenerator.strategy(),
                      TypeFactory.basic(field.getType().getName()),
                      properties);
          generatorCache.get(entityClass).put(field.getName(), generator);
        }
      }
    }
  }

  public static boolean reassociateIfUninitializedProxy(Object object, SessionImplementor source) {
    return source.getPersistenceContext().reassociateIfUninitializedProxy(object);
  }
}
