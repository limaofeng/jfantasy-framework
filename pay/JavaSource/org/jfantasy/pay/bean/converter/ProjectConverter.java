package org.jfantasy.pay.bean.converter;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.pay.bean.Project;

import javax.persistence.AttributeConverter;

public class ProjectConverter implements AttributeConverter<Project, String> {

    @Override
    public String convertToDatabaseColumn(Project attribute) {
        if (attribute == null) {
            return null;
        }
        return JSON.serialize(attribute, "creator", "create_time", "modifier", "modify_time", "description");
    }

    @Override
    public Project convertToEntityAttribute(String dbData) {
        if (StringUtil.isBlank(dbData)) {
            return null;
        }
        return JSON.deserialize(dbData, Project.class);
    }

}
