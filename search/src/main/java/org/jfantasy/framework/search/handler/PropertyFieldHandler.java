package org.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import org.jfantasy.framework.search.Document;
import org.jfantasy.framework.search.annotations.FieldType;
import org.jfantasy.framework.util.reflect.Property;

public class PropertyFieldHandler extends AbstractFieldHandler {
  public PropertyFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  public PropertyFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  @Override
  public void handle(Document doc) {
    process(doc);
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    process(typeMapping);
  }

  protected void process(TypeMapping.Builder typeMapping) {
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      processArray(typeMapping);
    } else {
      processPrimitive(typeMapping);
    }
  }

  protected void process(Document doc) {
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      processArray(doc);
    } else {
      processPrimitive(doc);
    }
  }

  private void processArray(Document doc) {
    Object objValue = this.property.getValue(this.obj);
    if (objValue == null) {
      return;
    }
    Class<?> type = this.property.getPropertyType();

    doc.add(fieldName, objValue);
  }

  private void processPrimitive(Document doc) {
    Object objValue = this.property.getValue(this.obj);
    if (objValue == null) {
      return;
    }
    Class<?> type = this.property.getPropertyType();

    if (FieldType.Keyword == fieldType) {
      doc.add(fieldName, objValue);
    } else if (FieldType.Text == fieldType) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Boolean) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Integer) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Long) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Short) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Float) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Double) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Date) {
      doc.add(fieldName, objValue);
    } else if (fieldType == FieldType.Date_Nanos) {
      doc.add(fieldName, objValue);
    }
  }

  private void processArray(TypeMapping.Builder typeMapping) {
    Class<?> type = this.property.getPropertyType();
    String fieldName = this.prefix + this.property.getName();

    boolean store = indexProperty.store();
    boolean index = indexProperty.index();

    typeMapping.properties(
        fieldName, builder -> builder.text(builder1 -> builder1.store(store).index(index)));
  }

  private void processPrimitive(TypeMapping.Builder typeMapping) {
    Class<?> type = this.property.getPropertyType();

    boolean store = indexProperty.store();
    boolean index = indexProperty.index();

    typeMapping.properties(
        fieldName,
        builder -> {
          if (FieldType.Keyword == fieldType) {
            builder.keyword(
                builder1 -> {
                  builder1.store(store).index(index);
                  return builder1;
                });
          } else if (FieldType.Text == fieldType) {
            builder.text(
                builder1 -> {
                  builder1.store(store).index(index);
                  return builder1;
                });
          } else if (fieldType == FieldType.Boolean) {
            builder.boolean_(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Integer) {
            builder.integer(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Long) {
            builder.long_(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Short) {
            builder.short_(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Float) {
            builder.float_(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Double) {
            builder.double_(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Date) {
            builder.date(builder1 -> builder1.store(store));
          } else if (fieldType == FieldType.Date_Nanos) {
            builder.dateNanos(builder1 -> builder1.store(store));
          }
          return builder;
        });
  }
}
