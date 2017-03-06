package org.jfantasy.framework.spring.mvc.jackson.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.framework.util.common.NumberUtil;

import java.io.IOException;

public class IdSerializer extends JsonSerializer<Long> {

    @Override
    public void serialize(Long value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeString(NumberUtil.toHex(value.intValue() * 20170302));
    }
}
