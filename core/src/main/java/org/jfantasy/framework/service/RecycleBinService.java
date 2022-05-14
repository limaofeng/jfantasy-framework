package org.jfantasy.framework.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.JSON;

@Slf4j
public final class RecycleBinService {

  public <T> void recycle(T object) {
    log.debug(JSON.serialize(object));
  }

  public <T> T recover(String filePath, Class<T> clazz) {
    return JSON.deserialize("", clazz);
  }

  public <T> List<T> getHistorys(Long id, Class<T> clazz) {
    return null;
  }
}
