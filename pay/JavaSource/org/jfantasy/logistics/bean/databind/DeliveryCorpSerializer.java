package org.jfantasy.logistics.bean.databind;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.logistics.bean.Express;

import java.io.IOException;

public class DeliveryCorpSerializer extends JsonSerializer<Express> {

    @Override
    public void serialize(Express express, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (express == null) {
            jgen.writeNull();
        } else {
            jgen.writeNumber(express.getId());
        }
    }

}
