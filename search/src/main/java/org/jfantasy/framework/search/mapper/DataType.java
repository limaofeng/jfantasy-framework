package org.jfantasy.framework.search.mapper;

import org.jfantasy.framework.search.annotations.FieldType;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class DataType {
  private DataType() {}

  public static boolean isString(Class<?> type) {
    return type.equals(String.class);
  }

  public static boolean isInteger(Class<?> type) {
    return type.equals(Integer.TYPE);
  }

  public static boolean isIntegerObject(Class<?> type) {
    return type.equals(Integer.class);
  }

  public static boolean isLong(Class<?> type) {
    return type.equals(Long.TYPE);
  }

  public static boolean isLongObject(Class<?> type) {
    return type.equals(Long.class);
  }

  public static boolean isShort(Class<?> type) {
    return type.equals(Short.TYPE);
  }

  public static boolean isShortObject(Class<?> type) {
    return type.equals(Short.class);
  }

  public static boolean isFloat(Class<?> type) {
    return type.equals(Float.TYPE);
  }

  public static boolean isFloatObject(Class<?> type) {
    return type.equals(Float.class);
  }

  public static boolean isDouble(Class<?> type) {
    return type.equals(Double.TYPE);
  }

  public static boolean isDoubleObject(Class<?> type) {
    return type.equals(Double.class);
  }

  public static boolean isBoolean(Class<?> type) {
    return type.equals(Boolean.TYPE);
  }

  public static boolean isBooleanObject(Class<?> type) {
    return type.equals(Boolean.class);
  }

  public static boolean isChar(Class<?> type) {
    return type.equals(Character.TYPE);
  }

  public static boolean isCharObject(Class<?> type) {
    return type.equals(Character.class);
  }

  public static boolean isDate(Class<?> type) {
    return type.equals(Date.class);
  }

  public static boolean isTimestamp(Class<?> type) {
    return type.equals(Timestamp.class);
  }

  public static boolean isList(Class<?> type) {
    return (type.equals(List.class))
        || (type.equals(ArrayList.class))
        || (type.equals(LinkedList.class));
  }

  public static boolean isSet(Class<?> type) {
    return (type.equals(Set.class)) || (type.equals(HashSet.class)) || (type.equals(TreeSet.class));
  }

  public static boolean isMap(Class<?> type) {
    return (type.equals(Map.class)) || (type.equals(HashMap.class)) || (type.equals(TreeMap.class));
  }

  public static boolean isEnum(Class<?> type) {
    return type.isEnum();
  }

  public static boolean isBigDecimal(Class<?> type) {
    return BigDecimal.class.isAssignableFrom(type);
  }

  public static FieldType getFieldType(Class<?> type) {
    if (type.isEnum()) {
      return FieldType.Keyword;
    }
    if (DataType.isString(type)) {
      return FieldType.Text;
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
  }
}
