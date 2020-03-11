package org.jfantasy.framework.jackson.deserializer;

import org.jfantasy.framework.dao.hibernate.util.ReflectionUtils;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.StringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.Date;

public class DateDeserializer extends JsonDeserializer<Date> {

    static final Log LOG = LogFactory.getLog(DateDeserializer.class);

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
                LOG.debug("不能转换日期格式[" + value + "]", e);
                throw new IgnoreException(e.getMessage(), e);
            }
        }
        throw new IgnoreException("JsonToken = " + t + ",是不能处理的类型！");
    }

}
