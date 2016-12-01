package org.jfantasy.logistics.bean.databind;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.logistics.bean.DeliveryType;
import org.jfantasy.framework.util.common.StringUtil;

import java.io.IOException;

public class DeliveryTypeDeserializer extends JsonDeserializer<DeliveryType> {

    @Override
    public DeliveryType deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        Long value = jp.getValueAsLong();
        if (StringUtil.isBlank(value)) {
            return null;
        }
        return new DeliveryType(value);
    }

}
