package org.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import org.jfantasy.framework.search.DocumentData;
import org.jfantasy.framework.search.exception.IdException;
import org.jfantasy.framework.util.reflect.Property;

public class IdFieldHandler extends AbstractFieldHandler {

  public IdFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  public IdFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  @Override
  public void handle(DocumentData doc) {
    String fieldName = this.property.getName();
    doc.add(fieldName, getEntityId());
    // doc.add(new Field(fieldName, getEntityId(), Field.Store.YES,
    // Field.Index.NOT_ANALYZED));
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {}

  private String getEntityId() {
    Object id = this.property.getValue(this.obj);
    if (id == null) {
      throw new IdException("要索引的对象@Id字段不能为空");
    }
    return String.valueOf(id);
  }
}
