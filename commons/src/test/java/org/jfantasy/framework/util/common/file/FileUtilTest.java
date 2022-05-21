package org.jfantasy.framework.util.common.file;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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

  @Test
  void getMimeType() throws IOException {
    File file = new File("/Users/limaofeng/Workspace/framework/core/src/test/resources/banner.txt");
    String mimeType = FileUtil.getMimeType(file);
    System.out.println(mimeType);
    System.out.println(Files.probeContentType(file.toPath()));
  }
}
