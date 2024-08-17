package net.asany.jfantasy.framework.util.common.file;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

public interface ArchiveHandler {
  /**
   * 压缩文件
   *
   * @param sourceDir 源文件夹
   * @param outputFilePath 输出文件
   * @param options 压缩选项
   * @throws IOException 异常
   */
  void compress(Path sourceDir, Path outputFilePath, CompressionOptions options) throws IOException;

  void compress(Path sourceDir, OutputStream output, CompressionOptions options) throws IOException;

  /**
   * 解压文件
   *
   * @param sourceFilePath 源压缩文件
   * @param destDir 解压目录
   * @throws IOException 异常
   */
  void decompress(Path sourceFilePath, Path destDir) throws Exception;
}
