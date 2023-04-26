package org.jfantasy.framework.util;

import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.error.ExecuteCommandException;
import org.jfantasy.framework.error.ImageProcessingException;
import org.junit.jupiter.api.Test;

@Slf4j
class FFmpegTest {

  @Test
  void image2() throws ExecuteCommandException, TimeoutException, ImageProcessingException {
    String path = "/Users/limaofeng/Downloads/测试影片2.mp4";

    long length = FFmpeg.duration(path);

    log.debug(" 视频长度: " + length);

    long location = (length / 60) > 14 ? 60 : 30;

    do {

      String imagPath = FFmpeg.image2(path, location);

      Images.ImageMetadata metadata = Images.identify(imagPath);

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
