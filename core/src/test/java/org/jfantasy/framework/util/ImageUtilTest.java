package org.jfantasy.framework.util;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class ImageUtilTest {

  @Test
  void resize() {
    String target = ImageUtil.resize("/Users/limaofeng/Downloads/测试文件名.jpg", "150x150");
    assert new File(target).length() > 0;
    log.debug(target);
  }

  @Test
  void identify() {
    ImageUtil.ImageMetadata metadata = ImageUtil.identify("/Users/limaofeng/Downloads/测试文件名.jpg");
    log.debug(metadata.toString());
  }
}
