package org.jfantasy.framework.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * 图片处理工具类
 *
 * @author limaofeng
 */
public class Images {

  public static String base64(String imagePath) throws IOException {
    // 读取图像文件
    byte[] bytes = Files.readAllBytes(Paths.get(imagePath));
    // 将字节数组编码为 Base64 字符串
    return Base64.getEncoder().encodeToString(bytes);
  }
}
