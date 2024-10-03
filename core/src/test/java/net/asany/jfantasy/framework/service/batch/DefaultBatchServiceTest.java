/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
