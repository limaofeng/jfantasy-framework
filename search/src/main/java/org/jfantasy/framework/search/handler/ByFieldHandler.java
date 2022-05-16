package org.jfantasy.framework.search.handler;

import java.util.List;
import org.jfantasy.framework.search.DocumentData;
import org.jfantasy.framework.util.reflect.Property;

public abstract class ByFieldHandler extends PropertyFieldHandler {

  protected ByFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  protected ByFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  protected void processList(
      List<?> objList, DocumentData doc, boolean analyze, boolean store, double boost) {
    StringBuilder sb = new StringBuilder();
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      for (Object o : objList) {
        Object value = this.property.getValue(o);
        if (value != null) {
          sb.append(getArrayString(value, type.getComponentType())).append(";");
        }
      }
    } else {
      for (Object o : objList) {
        Object value = this.property.getValue(o);
        if (value != null) {
          sb.append(value).append(";");
        }
      }
    }
    doc.add(fieldName, sb.toString());
  }
}
