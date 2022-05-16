package org.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jfantasy.framework.search.DocumentData;
import org.jfantasy.framework.search.annotations.IndexEmbedBy;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.search.mapper.DataType;
import org.jfantasy.framework.util.reflect.Property;

public class EmbedListFieldHandler extends AbstractFieldHandler {

  public EmbedListFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  public EmbedListFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  @Override
  public void handle(DocumentData doc) {
    Object value = this.property.getValue(this.obj);
    if (value == null) {
      return;
    }
    List<Object> list = null;
    Class<?> clazz;
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      clazz = type.getComponentType();
      int len = Array.getLength(value);
      list = new ArrayList<>();
      for (int i = 0; i < len; i++) {
        list.add(Array.get(value, i));
      }
    } else {
      ParameterizedType paramType = this.property.getGenericType();
      Type[] types = paramType.getActualTypeArguments();
      if (types.length == 1) {
        clazz = (Class<?>) types[0];
        if (DataType.isList(type)) {
          list = (List<Object>) value;
        } else if (DataType.isSet(type)) {
          Set<?> set = (Set<?>) value;
          list = new ArrayList<>(set);
        }
      } else if (types.length == 2) {
        clazz = (Class<?>) types[1];
        Map<?, ?> map = (Map<?, ?>) value;
        list = new ArrayList<>(map.values());
      } else {
        return;
      }
    }
    if ((list != null) && (!list.isEmpty())) {
      for (Property p : PropertysCache.getInstance().filter(clazz, IndexEmbedBy.class)) {
        FieldHandler handler = new EmbedByFieldHandler(this.obj.getClass(), list, p, this.prefix);
        handler.handle(doc);
      }
    }
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    Class<?> clazz;
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      clazz = type.getComponentType();
    } else {
      ParameterizedType paramType = this.property.getGenericType();
      Type[] types = paramType.getActualTypeArguments();
      if (types.length == 1) {
        clazz = (Class<?>) types[0];
      } else if (types.length == 2) {
        clazz = (Class<?>) types[1];
      } else {
        return;
      }
    }
    for (Property p : PropertysCache.getInstance().filter(clazz, IndexEmbedBy.class)) {
      FieldHandler handler = new EmbedByFieldHandler(p, this.prefix);
      handler.handle(typeMapping);
    }
  }
}
