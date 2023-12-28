package net.asany.jfantasy.framework.util;

import java.util.concurrent.TimeoutException;
import net.asany.jfantasy.framework.error.ExecuteCommandException;
import net.asany.jfantasy.framework.util.common.DateUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.common.file.FileUtil;

/**
 * FFmpeg 工具类
 *
 * @author limaofeng
 */
public class FFmpeg {

  public static String image2(String path, long location)
      throws ExecuteCommandException, TimeoutException {
    String target = FileUtil.tmpdir() + StringUtil.uuid() + ".jpeg";
    String start = DateUtil.format(location);
    String command =
        String.format(
            "ffmpeg -ss %s -i \"%s\" -r 1 -frames:v 1 -f image2 \"%s\"", start, path, target);
    CommandUtil.executeCommand("/bin/bash", "-c", command);
    return target;
  }

  public static long duration(String path) throws ExecuteCommandException, TimeoutException {
    String command =
        String.format(
            "ffprobe -v error -show_entries format=duration -of default=noprint_wrappers=1:nokey=1 -i \"%s\"",
            path);
    String result = CommandUtil.executeCommand("/bin/bash", "-c", command);
    return Double.valueOf(result.replaceAll("[\n]$", "")).longValue();
  }
}
