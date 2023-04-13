package org.jfantasy.framework.service.batch;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
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
    defaultBatchService.setWorkerNumber(1);
    log.debug("worker number: {}", defaultBatchService.getWorkerNumber());
  }
}
