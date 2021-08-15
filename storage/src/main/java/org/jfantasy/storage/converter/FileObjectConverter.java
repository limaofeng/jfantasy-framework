package org.jfantasy.storage.converter;

import javax.persistence.AttributeConverter;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.storage.FileObject;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-09 18:17
 */
public class FileObjectConverter implements AttributeConverter<FileObject, String> {

  @Override
  public String convertToDatabaseColumn(FileObject attribute) {
    if (attribute == null) {
      return null;
    }
    return JSON.serialize(attribute);
  }

  @Override
  public FileObject convertToEntityAttribute(String dbData) {
    if (StringUtil.isBlank(dbData)) {
      return null;
    }
    return JSON.deserialize(dbData, FileObject.class);
  }
}
