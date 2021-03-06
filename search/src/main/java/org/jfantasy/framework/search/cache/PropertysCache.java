package org.jfantasy.framework.search.cache;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.Id;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.search.annotations.IndexProperty;
import org.jfantasy.framework.search.exception.IdException;
import org.jfantasy.framework.search.exception.PropertyException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.reflect.Property;

@Slf4j
public class PropertysCache {
  private static final PropertysCache instance = new PropertysCache();

  private final Map<Class<?>, Map<String, Property>> fields = new HashMap<>();

  private PropertysCache() {}

  public static PropertysCache getInstance() {
    return instance;
  }

  public Property[] get(Class<?> clazz) {
    return ClassUtil.getProperties(clazz);
  }

  public <T extends Annotation> Property[] filter(Class<?> clazz, Class<T> tClass) {
    List<Property> properties = new ArrayList<>();
    for (Property p : this.get(clazz)) {
      if (p.getAnnotation(tClass) != null) {
        properties.add(p);
      }
    }
    return properties.toArray(new Property[0]);
  }

  public Property getIdProperty(Class<?> clazz) throws IdException {
    Property result = null;
    Property[] properties = get(clazz);
    for (Property p : properties) {
      if (p.getAnnotation(Id.class) != null) {
        result = p;
        break;
      }
    }
    if (result == null) {
      throw new IdException(clazz.getName() + " does not contain @Id field.");
    }
    return result;
  }

  public String getIdPropertyName(Class<?> clazz) {
    String name = null;
    Property f = null;
    try {
      f = getIdProperty(clazz);
    } catch (IdException ex) {
      log.error(ex.getMessage(), ex);
    }
    if (f != null) {
      name = f.getName();
    }
    return name;
  }

  public Property getProperty(Class<?> clazz, String fieldName) throws PropertyException {
    if (fieldName.contains(".")) {
      return PropertysCache.getInstance().getProperty(clazz, fieldName.split("\\.")[0]);
    }
    Property property = null;
    Property[] properties = get(clazz);
    for (Property p : properties) {
      if (p.getName().equals(fieldName)) {
        property = p;
        break;
      }
    }
    if (property == null) {
      throw new PropertyException("Field '" + fieldName + "' does not exists!");
    }
    return property;
  }

  public Property getPropertyByFieldName(Class<?> clazz, String fieldName) {
    if (!fields.containsKey(clazz)) {
      Map<String, Property> propertyMap = new HashMap<>();
      for (Property property : filter(clazz, IndexProperty.class)) {
        IndexProperty indexProperty = property.getAnnotation(IndexProperty.class);
        propertyMap.put(
            StringUtil.defaultValue(indexProperty.name(), property.getName()), property);
      }
      fields.put(clazz, propertyMap);
    }
    return fields.get(clazz).get(fieldName);
  }
}
