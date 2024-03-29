package org.jfantasy.framework.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.StringUtil;

@Slf4j
public class DateDeserializer extends JsonDeserializer<Date> {

  @Override
  public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
    JsonToken t = jp.getCurrentToken();
    if (t == JsonToken.VALUE_NUMBER_INT) {
      return new Date(jp.getNumberValue().longValue());
    } else if (t == JsonToken.VALUE_STRING) {
      String value = jp.getText().trim();
      if (StringUtil.isBlank(value)) {
        return null;
      }
      try {
        return ReflectionUtils.convertStringToObject(value, Date.class);
      } catch (Exception e) {
        log.debug("不能转换日期格式[" + value + "]", e);
        throw new IgnoreException(e.getMessage(), e);
      }
    }
    throw new IgnoreException("JsonToken = " + t + ",是不能处理的类型！");
  }
}
