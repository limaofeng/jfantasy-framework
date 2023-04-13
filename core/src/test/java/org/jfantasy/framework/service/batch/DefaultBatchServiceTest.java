package org.jfantasy.framework.service.batch;

import org.junit.jupiter.api.Test;

class DefaultBatchServiceTest {

  private final DefaultBatchService<String, String> defaultBatchService =
      new DefaultBatchService<>(
          (messages) -> {
            return messages;
          },
          10,
          10);

  @Test
  void setWorkerNumber() {
    defaultBatchService.setWorkerNumber(11);
  }
}
