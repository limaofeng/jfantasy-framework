package net.asany.jfantasy.framework.util.userstamp;

import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
public class RandomTypeTest {

  @Test
  public void randomType() {
    int[][] test = new int[31][10];
    for (int i = 0; i < 31; i++) {
      for (int j = 0; j < 10; j++) {
        test[i][j] = -1;
      }
    }
    Random r = new Random();

    for (int i = 0; i < 31; i++) {
      int t = 0;
      while (t < 12) {
        int n = r.nextInt(10);
        if (test[i][n] == -1) {
          if ((t == 2) || (t == 6)) {
            t++;
          }
          test[i][n] = (t++);
        }
      }
    }
    for (int i = 0; i < 31; i++) {
      log.debug("{");
      for (int j = 0; j < 10; j++) {
        log.debug(test[i][j] + ", ");
      }
      log.debug("}, ");
    }
  }

  @Test
  public void random() {
    Random random = new Random();
    for (int i = 0; i < 6; i++) {
      System.out.println(random.nextInt(10));
    }
  }
}
