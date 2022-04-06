package org.jfantasy.framework.util;

import java.io.File;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.yaml.snakeyaml.Yaml;

@Slf4j
public class ImageUtil {

  private static final Yaml YAML = new Yaml();
  private static final OgnlUtil OGNL_UTIL = OgnlUtil.getInstance();

  public static String resize(String source, String size) {
    String target = FileUtil.tmpdir() + StringUtil.uuid();
    String command = String.format("magick %s -resize %s %s", source, size, target);
    CommandUtil.exec(command);
    return target;
  }

  public static String resize(String path, int width, int height) {
    String target = FileUtil.tmpdir() + File.separator + StringUtil.uuid();
    String command = String.format("convert %s -resize %dx%d %s", path, width, height, target);
    resize(path, width + "x" + height);
    return target;
  }

  public static ImageMetadata identify(String path) {
    String info = CommandUtil.exec(String.format("magick identify -verbose %s", path));
    Map<String, Object> loadData = YAML.loadAs(info, Map.class);
    String[] size =
        StringUtil.tokenizeToStringArray(OGNL_UTIL.getValue("Image.Geometry", loadData), "x+");
    return ImageMetadata.builder()
        .format(OGNL_UTIL.getValue("Image.Format", loadData))
        .mimeType(OGNL_UTIL.getValue("Image[\"Mime type\"]", loadData))
        .size(size[0] + "x" + size[1])
        .fileSize(OGNL_UTIL.getValue("Image.Filesize", loadData))
        .ChannelStatistics(
            ChannelStatistics.builder()
                .blue(
                    ColorChannel.builder()
                        .mean(OGNL_UTIL.getValue("Image['Channel statistics'].Blue.mean", loadData))
                        .build())
                .red(
                    ColorChannel.builder()
                        .mean(OGNL_UTIL.getValue("Image['Channel statistics'].Red.mean", loadData))
                        .build())
                .green(
                    ColorChannel.builder()
                        .mean(
                            OGNL_UTIL.getValue("Image['Channel statistics'].Green.mean", loadData))
                        .build())
                .pixels(OGNL_UTIL.getValue("Image['Channel statistics'].Pixels", loadData))
                .build())
        .build();
  }

  @Getter
  @Builder
  @ToString
  public static class ImageMetadata {
    private String format;
    private String mimeType;
    private String size;
    private String fileSize;
    private ChannelStatistics ChannelStatistics;
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
