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
package net.asany.jfantasy.framework.util.cv;

import groovy.util.logging.Slf4j;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class RtspUtils {

  private static final Logger log = LoggerFactory.getLogger(RtspUtils.class);

  static {
    Loader.load(org.bytedeco.ffmpeg.global.avutil.class);
  }

  private static final long SAVE_INTERVAL = 1000000; // 保存间隔，单位为微秒，例如这里设置为1秒

  @Test
  public void rtsp() {
    String rtspUrl = "rtsp://admin:wap9000f@192.168.120.50:554/Streaming/channels/101";
    try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(rtspUrl)) {
      grabber.start();

      long lastSavedTimestamp = 0; // 上一次保存帧的时间戳
      Frame frame;
      while ((frame = grabber.grabImage()) != null) {
        if (frame.timestamp - lastSavedTimestamp >= SAVE_INTERVAL) {
          // 如果当前帧的时间戳与上一次保存的时间戳之差大于或等于保存间隔
          saveFrameAsImage(frame, formatVideoTime(frame.timestamp) + ".png");
          lastSavedTimestamp = frame.timestamp; // 更新上次保存的时间戳
        }
      }
    } catch (Exception e) {
      log.error(e.getMessage(), e);
    }
  }

  public static String formatVideoTime(long timestampMicroseconds) {
    long totalSeconds = timestampMicroseconds / 1000000; // 将微秒转换为秒
    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;
    long seconds = totalSeconds % 60;

    return String.format("%02d:%02d:%02d", hours, minutes, seconds);
  }

  public static void saveFrameAsImage(Frame frame, String fileName) {
    // 创建一个 Java2DFrameConverter 用于转换 Frame
    Java2DFrameConverter converter = new Java2DFrameConverter();
    BufferedImage image = converter.convert(frame);

    // 保存 BufferedImage 为文件
    File outputFile = new File("/Users/limaofeng/Workspace/framework/build/" + fileName);
    try {
      ImageIO.write(image, "png", outputFile);
      System.out.println("Image saved successfully.");
    } catch (IOException e) {
      System.out.println("Error saving image: " + e.getMessage());
    }
  }
}
