package net.asany.jfantasy.framework.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.ExecuteCommandException;
import net.asany.jfantasy.framework.error.ImageProcessingException;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.ognl.OgnlUtil;
import org.yaml.snakeyaml.Yaml;

/**
 * 图片处理工具类
 *
 * @author limaofeng
 */
@Slf4j
public class Images {

  public static String base64(String imagePath) throws IOException {
    // 读取图像文件
    byte[] bytes = Files.readAllBytes(Paths.get(imagePath));
    // 将字节数组编码为 Base64 字符串
    return Base64.getEncoder().encodeToString(bytes);
  }

  private static final Yaml YAML = new Yaml();
  private static final OgnlUtil OGNL_UTIL = OgnlUtil.getInstance();

  protected static String tmpdir() {
    return System.getProperty("java.io.tmpdir");
  }

  public static String resize(String source, String size) throws ImageProcessingException {
    return resize(source, size, tmpdir() + File.separator + StringUtil.uuid());
  }

  public static String resize(String source, String size, String target)
      throws ImageProcessingException {
    String command = String.format("magick %s -resize %s %s", source, size, target);
    try {
      CommandUtil.executeCommand(command);
    } catch (ExecuteCommandException | TimeoutException e) {
      log.error(e.getMessage(), e);
      throw new ImageProcessingException(e);
    }
    return target;
  }

  public static String resize(String path, int width, int height) throws ImageProcessingException {
    String target = tmpdir() + File.separator + StringUtil.uuid();
    resize(path, width + "x" + height);
    return target;
  }

  public static ImageMetadata identify(String path) throws ImageProcessingException {
    try {
      String info = CommandUtil.executeCommand(String.format("magick identify -verbose %s", path));
      Map<String, String> loadData = YAML.loadAs(info, Map.class);
      String[] size =
          StringUtil.tokenizeToStringArray(OGNL_UTIL.getValue("Image.Geometry", loadData), "x+");
      return ImageMetadata.builder()
          .format(OGNL_UTIL.getValue("Image.Format", loadData))
          .mimeType(OGNL_UTIL.getValue("Image[\"Mime type\"]", loadData))
          .size(size[0] + "x" + size[1])
          .fileSize(OGNL_UTIL.getValue("Image.Filesize", loadData))
          .channelStatistics(
              ChannelStatistics.builder()
                  .blue(
                      ColorChannel.builder()
                          .mean(
                              OGNL_UTIL.getValue("Image['Channel statistics'].Blue.mean", loadData))
                          .build())
                  .red(
                      ColorChannel.builder()
                          .mean(
                              OGNL_UTIL.getValue("Image['Channel statistics'].Red.mean", loadData))
                          .build())
                  .green(
                      ColorChannel.builder()
                          .mean(
                              OGNL_UTIL.getValue(
                                  "Image['Channel statistics'].Green.mean", loadData))
                          .build())
                  .pixels(OGNL_UTIL.getValue("Image['Channel statistics'].Pixels", loadData))
                  .build())
          .build();
    } catch (ExecuteCommandException | TimeoutException e) {
      throw new ImageProcessingException(e);
    }
  }

  @Getter
  @Builder
  @ToString
  public static class ImageMetadata {
    private String format;
    private String mimeType;
    private String size;
    private String fileSize;
    private ChannelStatistics channelStatistics;
  }

  @Getter
  @Builder
  public static class ColorChannel {
    private int min;
    private int max;
    private int mean;
    private int median;

    static class ColorChannelBuilder {
      public ColorChannelBuilder mean(String mean) {
        this.mean = Double.valueOf(mean.split(" ")[0].trim()).intValue();
        return this;
      }
    }
  }

  @Getter
  @Builder
  public static class ChannelStatistics {
    private int pixels;
    private ColorChannel red;
    private ColorChannel green;
    private ColorChannel blue;
  }
}
