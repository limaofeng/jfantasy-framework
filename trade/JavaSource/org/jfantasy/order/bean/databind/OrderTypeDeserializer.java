package org.jfantasy.order.bean.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.order.bean.OrderType;

import java.io.IOException;

public class OrderTypeDeserializer extends JsonDeserializer<OrderType> {

    @Override
    public OrderType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        OrderType orderType = new OrderType();
        orderType.setId(p.getValueAsString());
        return orderType;
    }

}
