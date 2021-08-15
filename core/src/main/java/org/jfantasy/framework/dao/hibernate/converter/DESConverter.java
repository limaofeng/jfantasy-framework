package org.jfantasy.framework.dao.hibernate.converter;

import javax.persistence.AttributeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.crypto.CryptoException;
import org.jfantasy.framework.crypto.DESPlus;

public class DESConverter implements AttributeConverter<String, String> {

  private static final Log LOG = LogFactory.getLog(DESConverter.class);

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
      LOG.debug(e.getMessage(), e);
      return attribute;
    }
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    try {
      return desPlus.decrypt(dbData);
    } catch (CryptoException e) {
      LOG.debug(e.getMessage(), e);
      return dbData;
    }
  }
}
