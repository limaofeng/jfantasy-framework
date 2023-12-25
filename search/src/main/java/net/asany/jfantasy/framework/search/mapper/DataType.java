package net.asany.jfantasy.framework.search.mapper;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import net.asany.jfantasy.framework.search.annotations.FieldType;

public class DataType {
  private DataType() {}

  public static boolean isString(Class<?> type) {
    return type.equals(String.class);
  }

  public static boolean isInteger(Class<?> type) {
    return type.equals(Integer.TYPE) || type.equals(Integer.class);
  }

  public static boolean isLong(Class<?> type) {
    return type.equals(Long.TYPE) || type.equals(Long.class);
  }

  public static boolean isShort(Class<?> type) {
    return type.equals(Short.TYPE) || type.equals(Short.class);
  }

  public static boolean isFloat(Class<?> type) {
    return type.equals(Float.TYPE) || type.equals(Float.class);
  }

  public static boolean isDouble(Class<?> type) {
    return type.equals(Double.TYPE) || type.equals(Double.class);
  }

  public static boolean isBoolean(Class<?> type) {
    return type.equals(Boolean.TYPE) || type.equals(Boolean.class);
  }

  public static boolean isChar(Class<?> type) {
    return type.equals(Character.TYPE) || type.equals(Character.class);
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
    }
    if ((DataType.isBoolean(type))) {
      return FieldType.Boolean;
    }
    if ((DataType.isChar(type))) {
      return FieldType.Keyword;
    }
    if ((DataType.isInteger(type))) {
      return FieldType.Integer;
    }
    if ((DataType.isLong(type))) {
      return FieldType.Long;
    }
    if ((DataType.isShort(type))) {
      return FieldType.Short;
    }
    if ((DataType.isFloat(type))) {
      return FieldType.Float;
    }
    if ((DataType.isDouble(type))) {
      return FieldType.Double;
    }
    if (DataType.isDate(type)) {
      return FieldType.Date;
    }
    if (DataType.isTimestamp(type)) {
      return FieldType.Date_Nanos;
    }
    if ((DataType.isSet(type)) || (DataType.isList(type))) {
      return FieldType.Text;
    }
    if (DataType.isMap(type)) {
      return FieldType.Text;
    }
    if (DataType.isBigDecimal(type)) {
      return FieldType.Double;
    }
    return FieldType.Text;
  }
}
