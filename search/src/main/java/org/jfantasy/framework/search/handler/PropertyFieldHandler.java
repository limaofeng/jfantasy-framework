package org.jfantasy.framework.search.handler;

import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import org.jfantasy.framework.search.DocumentData;
import org.jfantasy.framework.search.annotations.Field;
import org.jfantasy.framework.search.annotations.FieldType;
import org.jfantasy.framework.search.mapper.DataType;
import org.jfantasy.framework.util.reflect.Property;

public class PropertyFieldHandler extends AbstractFieldHandler {
  public PropertyFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  public PropertyFieldHandler(Object obj, Property property, String prefix) {
    super(obj, property, prefix);
  }

  @Override
  public void handle(DocumentData doc) {
    Field ip = this.property.getAnnotation(Field.class);
    process(doc, ip.index(), ip.store(), ip.boost());
  }

  @Override
  public void handle(TypeMapping.Builder typeMapping) {
    Field ip = this.property.getAnnotation(Field.class);
    process(typeMapping, field);
  }

  protected void process(TypeMapping.Builder typeMapping, Field field) {
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      processArray(typeMapping, analyze, store, boost);
    } else {
      processPrimitive(typeMapping, field);
    }
  }

  protected void process(DocumentData doc, boolean analyze, boolean store, double boost) {
    Class<?> type = this.property.getPropertyType();
    if (type.isArray()) {
      processArray(doc, analyze, store, boost);
    } else {
      processPrimitive(doc, analyze, store, boost);
    }
  }

  private void processArray(DocumentData doc, boolean analyze, boolean store, double boost) {
    Object objValue = this.property.getValue(this.obj);
    if (objValue == null) {
      return;
    }
    Class<?> type = this.property.getPropertyType();
    String fieldName = this.prefix + this.property.getName();
    // org.apache.lucene.document.Field f = new
    // org.apache.lucene.document.Field(fieldName,
    // getArrayString(objValue, type.getComponentType()), store ? Field.Store.YES :
    // Field.Store.NO,
    // analyze ? Field.Index.ANALYZED : Field.Index.NOT_ANALYZED);
    // f.setBoost(boost);
    // doc.add(f);
  }

  private void processPrimitive(DocumentData doc, boolean analyze, boolean store, double boost) {
    Object objValue = this.property.getValue(this.obj);
    if (objValue == null) {
      return;
    }
    Class<?> type = this.property.getPropertyType();
    String fieldName = this.prefix + this.property.getName();
    doc.add(fieldName, objValue);
    // IndexableField f = null;
    // if (DataType.isString(type) || type.isEnum()) { //TODO 枚举使用字符串的方式处理
    // f = new org.apache.lucene.document.Field(fieldName, objValue.toString(),
    // store ?
    // Field.Store.YES : Field.Store.NO, analyze ? Field.Index.ANALYZED :
    // Field.Index.NOT_ANALYZED);
    // } else if ((DataType.isBoolean(type)) || (DataType.isBooleanObject(type))) {
    // f = new org.apache.lucene.document.Field(fieldName, objValue.toString(),
    // store ?
    // Field.Store.YES : Field.Store.NO, Field.Index.NOT_ANALYZED);
    // } else if ((DataType.isChar(type)) || (DataType.isCharObject(type))) {
    // f = new org.apache.lucene.document.Field(fieldName, objValue.toString(),
    // store ?
    // Field.Store.YES : Field.Store.NO, Field.Index.NOT_ANALYZED);
    // } else if ((DataType.isInteger(type)) || (DataType.isIntegerObject(type))) {
    // int v = Integer.parseInt(objValue.toString());
    // f = new NumericDocValuesField(fieldName, store ? Field.Store.YES :
    // Field.Store.NO,
    // true).setIntValue(v);
    // } else if ((DataType.isLong(type)) || (DataType.isLongObject(type))) {
    // long v = Long.parseLong(objValue.toString());
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setLongValue(v);
    // } else if ((DataType.isShort(type)) || (DataType.isShortObject(type))) {
    // short v = Short.parseShort(objValue.toString());
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setIntValue(v);
    // } else if ((DataType.isFloat(type)) || (DataType.isFloatObject(type))) {
    // float v = Float.parseFloat(objValue.toString());
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setFloatValue(v);
    // } else if ((DataType.isDouble(type)) || (DataType.isDoubleObject(type))) {
    // double v = Double.parseDouble(objValue.toString());
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setDoubleValue(v);
    // } else if (DataType.isDate(type)) {
    // Date date = (Date) objValue;
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setLongValue(date.getTime());
    // } else if (DataType.isTimestamp(type)) {
    // Timestamp ts = (Timestamp) objValue;
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setLongValue(ts.getTime());
    // } else if ((DataType.isSet(type)) || (DataType.isList(type))) {
    // Collection<?> coll = (Collection<?>) objValue;
    // StringBuilder sb = new StringBuilder();
    // for (Object o : coll) {
    // sb.append(o).append(";");
    // }
    // f = new org.apache.lucene.document.Field(fieldName, sb.toString(), store ?
    // Field.Store.YES : Field.Store.NO, analyze ? Field.Index.ANALYZED :
    // Field.Index.NOT_ANALYZED);
    // } else if (DataType.isMap(type)) {
    // Map<?, ?> map = (Map<?, ?>) objValue;
    // StringBuilder sb = new StringBuilder();
    // for (Map.Entry<?, ?> entry : map.entrySet()) {
    // sb.append(entry.getValue()).append(";");
    // }
    // f = new org.apache.lucene.document.Field(fieldName, sb.toString(), store ?
    // Field.Store.YES : Field.Store.NO, analyze ? Field.Index.ANALYZED :
    // Field.Index.NOT_ANALYZED);
    // } else if (BigDecimal.class.equals(type)) {
    // f = new NumericField(fieldName, store ? Field.Store.YES : Field.Store.NO,
    // true).setDoubleValue(((BigDecimal) objValue).doubleValue());
    // }
    // if (f != null) {
    // f.setBoost(boost);
    // doc.add(f);
    // }
  }

  private void processArray(
      TypeMapping.Builder typeMapping, boolean analyze, boolean store, double boost) {
    Class<?> type = this.property.getPropertyType();
    String fieldName = this.prefix + this.property.getName();
    typeMapping.properties(
        fieldName,
        builder -> builder.text(builder1 -> builder1.store(store).index(analyze).boost(boost)));
  }

  private void processPrimitive(TypeMapping.Builder typeMapping, Field field) {
    Class<?> type = this.property.getPropertyType();
    String fieldName = this.prefix + this.property.getName();

    boolean store = field.store();
    boolean index = field.index();
    double boost = field.boost();

    FieldType fieldType = field.type();

    if(FieldType.Auto == fieldType) {
        DataType.getFieldType(type);
    }

    typeMapping.properties(
        fieldName,
        builder -> {
          if (DataType.isString(type) || type.isEnum()) {
            builder.text(
                builder1 -> {
                  builder1.store(store).index(index).boost(boost);
                  return builder1;
                });
          } else if ((DataType.isBoolean(type)) || (DataType.isBooleanObject(type))) {
            builder.boolean_(builder1 -> builder1.store(store));
          } else if ((DataType.isChar(type)) || (DataType.isCharObject(type))) {
            builder.text(builder1 -> builder1.store(store).index(false));
          } else if ((DataType.isInteger(type)) || (DataType.isIntegerObject(type))) {
            builder.integer(builder1 -> builder1.store(store));
          } else if ((DataType.isLong(type)) || (DataType.isLongObject(type))) {
            builder.long_(builder1 -> builder1.store(store));
          } else if ((DataType.isShort(type)) || (DataType.isShortObject(type))) {
            builder.short_(builder1 -> builder1.store(store));
          } else if ((DataType.isFloat(type)) || (DataType.isFloatObject(type))) {
            builder.float_(builder1 -> builder1.store(store));
          } else if ((DataType.isDouble(type)) || (DataType.isDoubleObject(type))) {
            builder.double_(builder1 -> builder1.store(store));
          } else if (DataType.isDate(type)) {
            builder.date(builder1 -> builder1.store(store));
          } else if (DataType.isTimestamp(type)) {
            builder.long_(builder1 -> builder1.store(store));
          } else if ((DataType.isSet(type)) || (DataType.isList(type))) {
            builder.text(builder1 -> builder1.store(store).index(analyze));
          } else if (DataType.isMap(type)) {
            builder.text(builder1 -> builder1.store(store).index(analyze));
          } else if (DataType.isBigDecimal(type)) {
            builder.text(builder1 -> builder1.store(store));
          }
          return builder;
        });
  }
}
