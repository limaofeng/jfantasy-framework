package org.jfantasy.framework.util.ognl.typeConverter;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;
import ognl.DefaultTypeConverter;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;

public class DateConverter extends DefaultTypeConverter {

  @Override
  public Object convertValue(
      Map context, Object target, Member member, String propertyName, Object value, Class toType) {
    if (Date.class.isAssignableFrom(toType)) {
      String dateFormatString;
      DateFormat dateFormat = ClassUtil.getParamAnno((Method) member, DateFormat.class);
      dateFormatString = dateFormat != null ? dateFormat.pattern() : "yyyy-MM-dd HH:mm:ss";
      return DateUtil.parse(
          StringUtil.nullValue(ClassUtil.isArray(value) ? Array.get(value, 0) : value),
          dateFormatString);
    }
    return super.convertValue(context, target, member, propertyName, value, toType);
  }
}
