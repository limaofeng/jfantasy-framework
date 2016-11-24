package org.jfantasy.order.bean.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.entity.OrderItem;

import javax.persistence.AttributeConverter;
import java.util.Collections;
import java.util.List;


public class OrderItemConverter implements AttributeConverter<List<OrderItem>, String> {

    @Override
    public String convertToDatabaseColumn(List<OrderItem> attribute) {
        if (attribute == null) {
            return null;
        }
        return JSON.serialize(attribute);
    }

    @Override
    public List<OrderItem> convertToEntityAttribute(String dbData) {
        if (StringUtil.isNotBlank(dbData)) {
            return JSON.deserialize(dbData, new TypeReference<List<OrderItem>>() {
            });
        }
        return Collections.emptyList();
    }

}
