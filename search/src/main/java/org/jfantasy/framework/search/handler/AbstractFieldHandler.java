package org.jfantasy.framework.search.handler;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.Date;
import javax.persistence.Id;
import org.jfantasy.framework.search.annotations.FieldType;
import org.jfantasy.framework.search.annotations.IndexProperty;
import org.jfantasy.framework.search.mapper.DataType;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.reflect.Property;

public abstract class AbstractFieldHandler implements FieldHandler {
  protected static final String JOIN = ";";
  protected Object obj;
  protected Property property;
  protected String prefix;
  protected IndexProperty indexProperty;
  protected FieldType fieldType;
  protected String fieldName;

  protected AbstractFieldHandler(Property property, String prefix) {
    this.property = property;
    this.prefix = prefix;
    this.indexProperty = this.property.getAnnotation(IndexProperty.class);

    boolean isId = this.indexProperty == null && this.property.getAnnotation(Id.class) != null;
    Class<?> type = this.property.getPropertyType();
    if (isId) {
      this.fieldType = FieldType.Keyword;
    } else if (type.isArray()) {
      this.fieldType = FieldType.Text;
    } else {
      this.fieldType =
          FieldType.Auto == indexProperty.type()
              ? DataType.getFieldType(type)
              : indexProperty.type();
    }
    this.fieldName =
        this.prefix
            + StringUtil.defaultValue(
                indexProperty == null ? "" : indexProperty.name(), this.property.getName());
  }

  protected AbstractFieldHandler(Object obj, Property property, String prefix) {
    this(property, prefix);
    this.obj = obj;
  }

  protected String getArrayString(Object value, Class<?> type) {
    StringBuilder sb = new StringBuilder();
    if (DataType.isDate(type)) {
      Date[] arr = (Date[]) value;
      for (Date e : arr) {
        sb.append(e.getTime()).append(";");
      }
    } else if (DataType.isTimestamp(type)) {
      Timestamp[] arr = (Timestamp[]) value;
      for (Timestamp e : arr) {
        sb.append(e.getTime()).append(";");
      }
    } else {
      int len = Array.getLength(value);
      for (int i = 0; i < len; i++) {
        sb.append(Array.get(value, i).toString()).append(";");
      }
    }
    return sb.toString();
  }
}
