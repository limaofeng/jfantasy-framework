package org.jfantasy.system.bean.databind;

import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.system.bean.DictKey;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class DataDictionaryKeyDeserializer extends JsonDeserializer<DictKey> {

    @Override
    public DictKey deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        String value = jp.getValueAsString();
        if (StringUtil.isBlank(value) || !value.contains(":"))
            return null;
        String[] values = value.split(":");
        return new DictKey(values[1],values[0]);
    }
}
