package org.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import org.jfantasy.framework.search.Document;
import org.jfantasy.framework.search.annotations.IndexEmbedBy;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.reflect.Property;

public class EmbedFieldHandler extends AbstractFieldHandler {

  public EmbedFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  public EmbedFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  @Override
  public void handle(Document doc) {
    Object embedObj = this.property.getValue(this.obj);
    if (embedObj == null) {
      return;
    }
    Class<?> clazz = ClassUtil.getRealType(this.property);
    for (Property p : PropertysCache.getInstance().filter(clazz, IndexEmbedBy.class)) {
      FieldHandler handler = new EmbedByFieldHandler(this.obj.getClass(), embedObj, p, this.prefix);
      handler.handle(doc);
    }
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    Class<?> clazz = ClassUtil.getRealType(this.property);
    for (Property p : PropertysCache.getInstance().filter(clazz, IndexEmbedBy.class)) {
      FieldHandler handler = new EmbedByFieldHandler(p, this.prefix);
      handler.handle(typeMapping);
    }
  }
}
