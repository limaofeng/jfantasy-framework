package org.jfantasy.framework.dao.hibernate.util.converters;

import java.time.OffsetDateTime;
import java.util.Date;
import org.apache.commons.beanutils.converters.DateTimeConverter;

public class DateConverter extends DateTimeConverter {

  public DateConverter() {
    super();
  }

  public DateConverter(Object defaultValue) {
    super(defaultValue);
  }

  @Override
  protected Class<?> getDefaultType() {
    return Date.class;
  }

  protected <T> T convertToType(Class<T> targetType, Object value) throws Exception {
    if (value instanceof String && ((String) value).contains("T") && targetType == Date.class) {
      return (T) Date.from(OffsetDateTime.parse(((String) value)).toInstant());
    }
    return super.convertToType(targetType, value);
  }
}
