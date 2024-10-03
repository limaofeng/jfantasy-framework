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
package net.asany.jfantasy.framework.util;

import java.util.concurrent.TimeoutException;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.ExecuteCommandException;
import net.asany.jfantasy.framework.error.ImageProcessingException;
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
