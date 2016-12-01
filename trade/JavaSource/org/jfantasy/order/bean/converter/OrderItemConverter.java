package org.jfantasy.order.bean.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.entity.OrderItemDTO;

import javax.persistence.AttributeConverter;
import java.util.Collections;
import java.util.List;


public class OrderItemConverter implements AttributeConverter<List<OrderItemDTO>, String> {

    @Override
    public String convertToDatabaseColumn(List<OrderItemDTO> attribute) {
        if (attribute == null) {
            return null;
        }
        return JSON.serialize(attribute);
    }

    @Override
    public List<OrderItemDTO> convertToEntityAttribute(String dbData) {
        if (StringUtil.isNotBlank(dbData)) {
            return JSON.deserialize(dbData, new TypeReference<List<OrderItemDTO>>() {
            });
        }
        return Collections.emptyList();
    }

}