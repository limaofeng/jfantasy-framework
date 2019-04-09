package org.jfantasy.storage.converter;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.storage.FileObject;

import javax.persistence.AttributeConverter;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-09 18:17
 */
public class FileObjectsConverter implements AttributeConverter<FileObject[], String> {

    @Override
    public String convertToDatabaseColumn(FileObject[] attribute) {
        if (attribute == null) {
            return null;
        }
        return JSON.serialize(attribute);
    }

    @Override
    public FileObject[] convertToEntityAttribute(String dbData) {
        if (StringUtil.isBlank(dbData)) {
            return null;
        }
        return JSON.deserialize(dbData, FileObject[].class);
    }
}
