package net.asany.jfantasy.framework.service.batch;

import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.service.BatchService;
import org.junit.jupiter.api.Test;

@Slf4j
class DefaultBatchServiceTest {

  private final DefaultBatchService<String, String> defaultBatchService =
      BatchService.create(
          "xxx",
          (messages) -> {
            try {
              Thread.sleep(10000);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
            //              throw new RuntimeException("test");
            return messages;
          },
          10,
          10);

  @Test
  void setWorkerNumber() {
    defaultBatchService.setWorkerNumber(1);
    log.debug("worker number: {}", defaultBatchService.getWorkerNumber());
  }

  @Test
  void testRun() {
    log.info("start");
    CompletableFuture<String> future =
        defaultBatchService
            .submit("123")
            .thenApply(
                (x) -> {
                  log.info("return value: {}", x);
                  return x;
                });
    try {
      log.info("wait" + future.isDone());
      String s = future.join();
      log.info("return value: {}", s);
    } catch (Exception e) {
      log.error("error: " + e.getMessage(), e);
    }
  }
}
