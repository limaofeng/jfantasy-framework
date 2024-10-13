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
package net.asany.jfantasy.framework.util.common;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.asany.jfantasy.framework.jackson.deserializer.DateConverter;
import net.asany.jfantasy.framework.util.ognl.OgnlUtil;
import net.asany.jfantasy.framework.util.reflect.Property;
import org.apache.commons.beanutils.BeanUtilsBean;

public class BeanUtil {

  static {
    DateConverter dc = new DateConverter();
    dc.setUseLocaleFormat(true);
    dc.setPatterns(
        new String[] {
          "yyyy-MM",
          "yyyy-MM-dd",
          "yyyy-MM-dd HH:mm:ss",
          "yyyy-MM-dd HH:mm",
          "yyyyMMdd",
          "yyyyMMddHHmmss",
        });
    BeanUtilsBean.getInstance().getConvertUtils().register(dc, Date.class);
  }

  private BeanUtil() {}

  public static void setValue(Object target, String fieldName, Object value) {
    ClassUtil.setValue(target, fieldName, value);
  }

  public static Object getValue(Object target, String fieldName) {
    return ClassUtil.getValue(target, fieldName);
  }

  public static <T> T copyProperties(T dest, Object orig, PropertyFilter filter) {
    OgnlUtil _ognlUtil = OgnlUtil.getInstance();
    if (dest == null || orig == null) {
      return dest;
    }
    Class<?> destClass = dest.getClass();
    Property[] properties = ClassUtil.getProperties(orig);
    for (Property property : properties) {
      if (!property.isRead()) {
        continue;
      }
      Property setProperty = ClassUtil.getProperty(destClass, property.getName());
      if (setProperty == null || !setProperty.isWrite()) {
        continue;
      }
      Object value = _ognlUtil.getValue(property.getName(), orig);
      if (filter.accept(property, value, dest)) {
        _ognlUtil.setValue(property.getName(), dest, filter.convertValue(property, value, dest));
      }
    }
    return dest;
  }

  public static <T> T copyProperties(T dest, Object orig, String... excludeProperties) {
    if (dest == null || orig == null) {
      return dest;
    }
    return copyProperties(dest, orig, new IgnorePropertyFilter(excludeProperties));
  }

  public static <T> T copyNotNull(T dest, Object orig) {
    return copyProperties(
        dest, orig, (Property property, Object value, Object _dest) -> value != null);
  }

  private static int length(Object value) {
    if (ClassUtil.isArray(value)) {
      return Array.getLength(value);
    }
    if (ClassUtil.isList(value)) {
      return ((List<?>) value).size();
    }
    return 0;
  }

  private static Object get(Object value, int i) {
    if (ClassUtil.isArray(value)) {
      return Array.get(value, i);
    }
    if (ClassUtil.isList(value)) {
      return ((List<?>) value).get(i);
    }
    return null;
  }

  public PropertyFilter exclude(String... properties) {
    return null;
  }

  public PropertyFilter include(String... properties) {
    return null;
  }

  public interface PropertyFilter {
    boolean accept(Property property, Object value, Object target);

    default Object convertValue(Property property, Object value, Object target) {
      return value;
    }
  }

  private static class IgnorePropertyFilter implements PropertyFilter {

    private final String[] propertyNames;

    public IgnorePropertyFilter(String... propertyNames) {
      this.propertyNames = propertyNames;
    }

    @Override
    public boolean accept(Property property, Object value, Object target) {
      return Arrays.stream(propertyNames).noneMatch(item -> item.equals(property.getName()));
    }
  }

  public static <T> T convertStringToObject(String value, Class<T> toType) {
    return toType.cast(BeanUtilsBean.getInstance().getConvertUtils().convert(value, toType));
  }

  public static <T> T convert(Object value, Class<T> toType) {
    return toType.cast(BeanUtilsBean.getInstance().getConvertUtils().convert(value, toType));
  }

  private static class AllowPropertyFilter implements PropertyFilter {

    private final String[] propertyNames;

    public AllowPropertyFilter(String... propertyNames) {
      this.propertyNames = propertyNames;
    }

    @Override
    public boolean accept(Property property, Object propDest, Object propOrig) {
      return Arrays.stream(propertyNames).allMatch(item -> item.equals(property.getName()));
    }
  }
}
