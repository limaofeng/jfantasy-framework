package org.jfantasy.framework.lucene.backend;

import org.jfantasy.framework.lucene.annotations.Compare;
import org.jfantasy.framework.lucene.mapper.DataType;
import org.jfantasy.framework.util.reflect.Property;

public class CompareChecker {
    private Object obj;

    public CompareChecker(Object obj) {
        this.obj = obj;
    }

    public boolean isFit(Property p, Compare compare, String value) {
        boolean fit = false;
        switch (compare) {
            case IS_EQUALS:
                fit = isEquals(p, value);
                break;
            case NOT_EQUALS:
                fit = notEquals(p, value);
                break;
            case GREATER_THAN:
                fit = greaterThan(p, value);
                break;
            case GREATER_THAN_EQUALS:
                fit = greaterThanEquals(p, value);
                break;
            case LESS_THAN:
                fit = lessThan(p, value);
                break;
            case LESS_THAN_EQUALS:
                fit = lessThanEquals(p, value);
                break;
            case IS_NULL:
                fit = isNull(p.getValue(this.obj));
                break;
            case NOT_NULL:
                fit = notNull(p.getValue(this.obj));
                break;
            default:
        }
        return fit;
    }

    private boolean isEquals(Property p, String value) {
        Object objValue = p.getValue(this.obj);
        if (objValue == null) {
            return false;
        }
        String objStr = objValue.toString();
        Class<?> type = p.getPropertyType();
        if (DataType.isString(type) || DataType.isEnum(type)) {// 字符串 or 枚举
            return value.equals(objStr);
        }
        if ((DataType.isBoolean(type)) || (DataType.isBooleanObject(type))) {
            return Boolean.parseBoolean(objStr) == Boolean.parseBoolean(value);
        }
        if ((DataType.isChar(type)) || (DataType.isCharObject(type))) {
            return objStr.charAt(0) == value.charAt(0);
        }
        if ((DataType.isInteger(type)) || (DataType.isIntegerObject(type))) {
            return Integer.parseInt(objStr) == Integer.parseInt(value);
        }
        if ((DataType.isLong(type)) || (DataType.isLongObject(type))) {
            return Long.parseLong(objStr) == Long.parseLong(value);
        }
        if ((DataType.isShort(type)) || (DataType.isShortObject(type))) {
            return Short.parseShort(objStr) == Short.parseShort(value);
        }
        if ((DataType.isFloat(type)) || (DataType.isFloatObject(type))) {
            return Float.compare(Float.parseFloat(objStr), Float.parseFloat(value)) == 0;
        }
        if (DataType.isDouble(type) || DataType.isDoubleObject(type)) {
            return Double.compare(Double.parseDouble(objStr), Double.parseDouble(value)) == 0;
        }
        return false;

    }

    private boolean notEquals(Property p, String value) {
        Object objValue = p.getValue(this.obj);
        return objValue != null && !isEquals(p, value);
    }

    private boolean greaterThan(Property p, String value) {
        Object objValue = p.getValue(this.obj);
        if (objValue == null) {
            return false;
        }
        String objStr = objValue.toString();
        Class<?> type = p.getPropertyType();
        if ((DataType.isInteger(type)) || (DataType.isIntegerObject(type))) {
            return Integer.parseInt(objStr) > Integer.parseInt(value);
        }
        if ((DataType.isLong(type)) || (DataType.isLongObject(type))) {
            return Long.parseLong(objStr) > Long.parseLong(value);
        }
        if ((DataType.isShort(type)) || (DataType.isShortObject(type))) {
            return Short.parseShort(objStr) > Short.parseShort(value);
        }
        if ((DataType.isFloat(type)) || (DataType.isFloatObject(type))) {
            return Float.parseFloat(objStr) > Float.parseFloat(value);
        }
        return ((DataType.isDouble(type)) || (DataType.isDoubleObject(type))) && Double.parseDouble(objStr) > Double.parseDouble(value);

    }

    private boolean greaterThanEquals(Property p, String value) {
        Object objValue = p.getValue(p);
        if (objValue == null) {
            return false;
        }
        String objStr = objValue.toString();
        Class<?> type = p.getPropertyType();
        if ((DataType.isInteger(type)) || (DataType.isIntegerObject(type))) {
            return Integer.parseInt(objStr) >= Integer.parseInt(value);
        }
        if ((DataType.isLong(type)) || (DataType.isLongObject(type))) {
            return Long.parseLong(objStr) >= Long.parseLong(value);
        }
        if ((DataType.isShort(type)) || (DataType.isShortObject(type))) {
            return Short.parseShort(objStr) >= Short.parseShort(value);
        }
        if ((DataType.isFloat(type)) || (DataType.isFloatObject(type))) {
            return Float.parseFloat(objStr) >= Float.parseFloat(value);
        }
        return ((DataType.isDouble(type)) || (DataType.isDoubleObject(type))) && Double.parseDouble(objStr) >= Double.parseDouble(value);

    }

    private boolean lessThan(Property p, String value) {
        Object objValue = p.getValue(this.obj);
        return objValue != null && !greaterThanEquals(p, value);
    }

    private boolean lessThanEquals(Property p, String value) {
        Object objValue = p.getValue(this.obj);
        return objValue != null && !greaterThan(p, value);
    }

    private boolean isNull(Object objValue) {
        return objValue == null;
    }

    private boolean notNull(Object objValue) {
        return !isNull(objValue);
    }
}
