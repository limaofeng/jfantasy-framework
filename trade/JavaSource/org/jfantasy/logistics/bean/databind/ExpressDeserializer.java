package org.jfantasy.logistics.bean.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.logistics.bean.Express;
import org.jfantasy.framework.util.common.StringUtil;

import java.io.IOException;

public class ExpressDeserializer extends JsonDeserializer<Express> {

    @Override
    public Express deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String value = jp.getValueAsString();
        if (StringUtil.isBlank(value)) {
            return null;
        }
        return new Express(value);
    }

}
