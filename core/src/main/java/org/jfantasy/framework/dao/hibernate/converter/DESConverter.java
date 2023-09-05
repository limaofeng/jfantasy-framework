package org.jfantasy.framework.dao.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.crypto.CryptoException;
import org.jfantasy.framework.crypto.DESPlus;

/**
 * DES加密转换器
 *
 * @author limaofeng
 */
@Slf4j
public class DESConverter implements AttributeConverter<String, String> {

  private static DESPlus desPlus;

  public DESConverter() {
    desPlus = new DESPlus();
  }

  public DESConverter(String key) {
    desPlus = new DESPlus(key);
  }

  @Override
  public String convertToDatabaseColumn(String attribute) {
    try {
      return desPlus.encrypt(attribute);
    } catch (CryptoException e) {
      log.debug(e.getMessage(), e);
      return attribute;
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    try {
      return desPlus.decrypt(dbData);
    } catch (CryptoException e) {
      log.debug(e.getMessage(), e);
      return dbData;
    }
  }
}
