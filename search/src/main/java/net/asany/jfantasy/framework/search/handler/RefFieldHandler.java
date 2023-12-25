package net.asany.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.annotations.IndexRefBy;
import net.asany.jfantasy.framework.search.cache.PropertysCache;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.reflect.Property;

public class RefFieldHandler extends AbstractFieldHandler {

  public RefFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  public RefFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  @Override
  public void handle(Document doc) {
    Object entity = this.property.getValue(this.obj);
    if (entity == null) {
      return;
    }
    Class<?> clazz = ClassUtil.getRealType(this.property);
    Property[] properties = PropertysCache.getInstance().get(clazz);
    for (Property p : properties) {
      IndexRefBy irb = p.getAnnotation(IndexRefBy.class);
      if (irb != null) {
        FieldHandler handler = new RefByFieldHandler(this.obj.getClass(), entity, p, this.prefix);
        handler.handle(doc);
      }
    }
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    Class<?> clazz = ClassUtil.getRealType(this.property);
    Property[] properties = PropertysCache.getInstance().get(clazz);
    for (Property p : properties) {
      IndexRefBy irb = p.getAnnotation(IndexRefBy.class);
      if (irb != null) {
        FieldHandler handler = new RefByFieldHandler(p, this.prefix);
        handler.handle(typeMapping);
      }
    }
  }

  private String getEntityId(Object obj) {
    Property property = PropertysCache.getInstance().getIdProperty(obj.getClass());
    return String.valueOf(property.getValue(obj));
  }
}
