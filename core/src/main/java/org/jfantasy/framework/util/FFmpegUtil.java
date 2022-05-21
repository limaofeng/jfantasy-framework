package org.jfantasy.framework.util;

import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;

public class FFmpegUtil {

  public static String image2(String path, long location) {
    String target = ImageUtil.tmpdir() + StringUtil.uuid() + ".jpeg";
    String start = DateUtil.format(location);
    String command =
        String.format(
            "ffmpeg -ss %s -i \"%s\" -r 1 -frames:v 1 -f image2 \"%s\"", start, path, target);
    CommandUtil.exec("/bin/bash", "-c", command);
    return target;
  }

  public static long duration(String path) {
    String command =
        String.format(
            "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 -i \"%s\"",
            path);
    String result = CommandUtil.exec("/bin/bash", "-c", command);
    return Double.valueOf(result.replaceAll("[\n]$", "")).longValue();
  }
}
