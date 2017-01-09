package org.jfantasy.order.bean.databind;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.jfantasy.order.bean.OrderCashFlow;

import java.io.IOException;

public class OrderCashFlowSerializer extends JsonSerializer<OrderCashFlow> {

    @Override
    public void serialize(OrderCashFlow cashFlow, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeObject(cashFlow.getId() != null ? cashFlow.getId() : "");
    }
    
}