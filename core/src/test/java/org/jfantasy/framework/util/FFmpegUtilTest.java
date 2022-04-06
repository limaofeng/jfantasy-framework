package org.jfantasy.framework.util;

import static org.junit.jupiter.api.Assertions.*;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class FFmpegUtilTest {

  @Test
  void image2() {
    String path = "/Users/limaofeng/Downloads/[电影天堂www.dytt89.com]美丽人生BD国意双语中英双字.mp4";

    long length = FFmpegUtil.duration(path);

    log.debug(" 视频长度: " + length);

    long location = (length / 60) > 14 ? 60 : 30;

    do {

      String imagPath = FFmpegUtil.image2(path, location);

      ImageUtil.ImageMetadata metadata = ImageUtil.identify(imagPath);

      int r = metadata.getChannelStatistics().getRed().getMean();
      int g = metadata.getChannelStatistics().getGreen().getMean();
      int b = metadata.getChannelStatistics().getBlue().getMean();

      double luma = 0.2126 * r + 0.7152 * g + 0.0722 * b;

      if (luma > 40) {
        break;
      }

      location += 60;
    } while (location < length);
  }
}
