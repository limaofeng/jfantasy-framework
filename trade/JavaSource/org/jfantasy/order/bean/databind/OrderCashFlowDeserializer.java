package org.jfantasy.order.bean.databind;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.bean.OrderCashFlow;

import java.io.IOException;

public class OrderCashFlowDeserializer extends JsonDeserializer<OrderCashFlow> {

    @Override
    public OrderCashFlow deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        if (StringUtil.isBlank(jp.getValueAsString())) {
            return null;
        }
        return new OrderCashFlow(jp.getValueAsLong());
    }

}