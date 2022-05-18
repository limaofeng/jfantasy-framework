package cn.asany.demo.service;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

@Slf4j
public class ScheduleTaskTest {

  private Timer timer;

  @BeforeEach
  void setUp() throws InterruptedException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }

  @AfterEach
  void tearDown() throws InterruptedException {
    Thread.sleep(TimeUnit.SECONDS.toMillis(10));
  }

  int count = 0;

  private void delayCommit() {
    // 延时 commit 逻辑
    if (timer != null) {
      timer.cancel();
    }
    timer = new Timer();
    timer.schedule(
        new TimerTask() {
          @SneakyThrows
          @Override
          public void run() {
            log.debug("count:" + count);
            log.debug("Test abort yourself");
            timer.cancel();
            Thread.sleep(3000);
            log.debug("Aborting yourself failed");
          }
        },
        1000);
  }

  public void run() {
    log.debug("count:" + ++count);
    this.delayCommit();
  }

  @SneakyThrows
  @Test
  public void test() {
    for (int i = 0; i < 10; i++) {
      Thread.sleep(300);
      new Thread(ScheduleTaskTest.this::run).start();
    }
  }
}
