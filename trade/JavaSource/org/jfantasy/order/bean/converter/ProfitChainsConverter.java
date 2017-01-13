package org.jfantasy.order.bean.converter;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.order.rest.models.ProfitChain;

import javax.persistence.AttributeConverter;
import java.util.Collections;
import java.util.List;

public class ProfitChainsConverter implements AttributeConverter<List<ProfitChain>, String> {

    @Override
    public String convertToDatabaseColumn(List<ProfitChain> attribute) {
        if (attribute == null) {
            return null;
        }
        return JSON.serialize(attribute);
    }

    @Override
    public List<ProfitChain> convertToEntityAttribute(String dbData) {
        if (StringUtil.isNotBlank(dbData)) {
            return JSON.deserialize(dbData, new TypeReference<List<ProfitChain>>() {
            });
        }
        return Collections.emptyList();
    }

}
