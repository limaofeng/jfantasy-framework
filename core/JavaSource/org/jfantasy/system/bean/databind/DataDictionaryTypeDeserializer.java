package org.jfantasy.system.bean.databind;

import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.system.bean.DictType;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class DataDictionaryTypeDeserializer extends JsonDeserializer<DictType> {

    @Override
    public DictType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = jp.getValueAsString();
        if (StringUtil.isBlank(value))
            return null;
        return new DictType(value);
    }
}