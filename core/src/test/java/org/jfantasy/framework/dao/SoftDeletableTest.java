package org.jfantasy.framework.dao;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class SoftDeletableTest {

  @Test
  void getDeletedFieldName() {
    String fieldName = SoftDeletable.getDeletedFieldName(SoftDeletableBaseBusEntity.class);
    log.debug("fieldName: {}", fieldName);
  }
}
