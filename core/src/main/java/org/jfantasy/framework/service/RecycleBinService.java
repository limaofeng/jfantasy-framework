package org.jfantasy.framework.service;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.JSON;

public final class RecycleBinService {

  private static final Log LOGGER = LogFactory.getLog(RecycleBinService.class);

  public <T> void recycle(T object) {
    LOGGER.debug(JSON.serialize(object));
  }

  public <T> T recover(String filePath, Class<T> clazz) {
    return JSON.deserialize("", clazz);
  }

  public <T> List<T> getHistorys(Long id, Class<T> clazz) {
    return null;
  }
}
