package org.jfantasy.system.bean.databind;

import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.system.bean.DictKey;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class DataDictionaryKeySerializer extends JsonSerializer<DictKey> {

    @Override
    public void serialize(DictKey dictKey, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        try {
            if (dictKey == null) {
                jgen.writeString("");
            } else {
                jgen.writeString(dictKey.getType() + ":" + dictKey.getCode());
            }
        } catch (Exception e) {
            jgen.writeString("");
            throw new IgnoreException(e.getMessage(), e);
        }
    }

}
