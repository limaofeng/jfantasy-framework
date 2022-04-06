package org.jfantasy.framework.util;

import java.io.*;
import lombok.Builder;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.StreamUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.common.file.FileUtil;

@Slf4j
public class ImageUtil {

  @SneakyThrows
  private static String exec(String command) {
    Process proc = Runtime.getRuntime().exec(command);

    InputStream stderr = proc.getErrorStream();
    InputStream stIn = proc.getInputStream();

    StringBuilder builder = new StringBuilder();

    BufferedReader in = new BufferedReader(new InputStreamReader(stIn));
    String line;
    while ((line = in.readLine()) != null) {
      builder.append(line).append("\n");
    }
    StreamUtil.closeQuietly(stIn);

    BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
    while ((line = br.readLine()) != null) {
      log.debug("Process exitValue:" + line);
    }
    int exitVal = proc.waitFor();

    StreamUtil.closeQuietly(stderr);
    log.debug("Process exitValue:" + exitVal);
    return builder.toString();
  }

  public static String resize(String source, String size) {
    String target = FileUtil.tmpdir() + StringUtil.uuid();
    String command = String.format("convert %s -resize %s %s", source, size, target);
    exec(command);
    return target;
  }

  public static String resize(String path, int width, int height) {
    String target = FileUtil.tmpdir() + File.separator + StringUtil.uuid();
    String command = String.format("convert %s -resize %dx%d %s", path, width, height, target);
    resize(path, width + "x" + height);
    return target;
  }

  public static ImageMetadata identify(String path) {
    String info = exec(String.format("magick identify -verbose %s", path));
    String[] lines = info.split("\n");
    String[] size = StringUtil.tokenizeToStringArray(lines[5].split(":")[1], "x+");
    return ImageMetadata.builder()
        .format(lines[2].split(":")[1].trim())
        .mimeType(lines[3].split(":")[1].trim())
        .size(size[0] + "x" + size[1])
        .fileSize(lines[81].split(":")[1].trim())
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
  }
}
