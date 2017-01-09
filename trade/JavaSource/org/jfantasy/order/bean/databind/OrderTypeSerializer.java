package org.jfantasy.order.bean.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.order.bean.OrderType;

import java.io.IOException;

public class OrderTypeSerializer extends JsonSerializer<OrderType> {

    @Override
    public void serialize(OrderType value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeObject(value.getId());
    }

}
