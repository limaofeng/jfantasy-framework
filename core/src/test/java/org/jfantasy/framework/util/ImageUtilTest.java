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
    ImageUtil.ImageMetadata metadata = ImageUtil.identify("/Users/limaofeng/Downloads/foo-1.jpeg");
    log.debug(metadata.toString());

    int r = metadata.getChannelStatistics().getRed().getMean();
    int g = metadata.getChannelStatistics().getGreen().getMean();
    int b = metadata.getChannelStatistics().getBlue().getMean();

    double luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

    if (luma < 40) {
      log.debug("图片偏黑");
    }
  }
}
