package org.jfantasy.framework.dao.hibernate.converter;

import org.jfantasy.framework.util.common.StringUtil;

import javax.persistence.AttributeConverter;
import java.util.ArrayList;
import java.util.List;

public class StringsConverter implements AttributeConverter<String[], String> {

    @Override
    public String convertToDatabaseColumn(String[] attribute) {
        if (attribute == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder("$");
        for (String tag : attribute) {
            builder.append(tag).append("$");
        }
        return builder.toString();
    }

    @Override
    public String[] convertToEntityAttribute(String dbData) {
        if (StringUtil.isBlank(dbData)) {
            return new String[0];
        }
        List<String> tags = new ArrayList<>();
        for (String str : StringUtil.tokenizeToStringArray(dbData, "$")) {
            if (StringUtil.isBlank(str)) {
                continue;
            }
            tags.add(str);
        }
        return tags.toArray(new String[tags.size()]);
    }
}
