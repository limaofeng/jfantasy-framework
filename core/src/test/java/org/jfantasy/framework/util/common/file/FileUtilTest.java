package org.jfantasy.framework.util.common.file;

import java.io.File;
import org.junit.jupiter.api.Test;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-09 17:05
 */
public class FileUtilTest {

  @Test
  public void createFolder() {
    System.out.println(new File("/tmp/a/b/c/d").mkdirs());
  }
}
