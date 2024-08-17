package net.asany.jfantasy.framework.util.common.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.zip.ZipInputStream;
import net.asany.jfantasy.framework.util.common.StreamUtil;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;

public class ZipArchiveHandler implements ArchiveHandler {

  @Override
  public void compress(Path sourceDir, Path outputFilePath, CompressionOptions options)
      throws IOException {
    compress(sourceDir, Files.newOutputStream(outputFilePath), options);
  }

  public void compress(Path sourceDir, OutputStream output, CompressionOptions options)
      throws IOException {
    try (ZipOutputStream zos = new ZipOutputStream(output)) {
      if (!Files.isDirectory(sourceDir)) {
        throw new IllegalArgumentException(sourceDir.getFileName() + " is not a directory");
      }
      Files.walkFileTree(
          sourceDir,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              addFileToZip(sourceDir.relativize(file).toString(), Files.newInputStream(file), zos);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
              return FileVisitResult.CONTINUE;
            }
          });
      zos.setEncoding(options.getEncoding());
      zos.setComment(options.getComment());
    }
  }

  public void addFileToZip(String filename, InputStream input, ZipOutputStream zos)
      throws IOException {
    ZipEntry zipEntry = new ZipEntry(filename);
    zos.putNextEntry(zipEntry);
    StreamUtil.copyThenClose(input, zos);
  }

  @Override
  public void decompress(Path sourceFilePath, Path destDir) throws Exception {
    try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(sourceFilePath))) {
      java.util.zip.ZipEntry zipEntry = zis.getNextEntry();
      while (zipEntry != null) {
        Path filePath = destDir.resolve(zipEntry.getName());
        if (zipEntry.isDirectory()) {
          Files.createDirectories(filePath);
        } else {
          Files.createDirectories(filePath.getParent()); // 确保父目录存在
          try (OutputStream fos = Files.newOutputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int length;
            long size = zipEntry.getSize();
            long bytesRead = 0;

            while ((length = zis.read(buffer)) != -1) {
              fos.write(buffer, 0, length);
              bytesRead += length;
              if (size != -1 && bytesRead >= size) {
                // 如果已经读取的字节数达到或超过了声明的大小，则可以认为已完成
                break;
              }
            }
          }
        }
        zipEntry = zis.getNextEntry();
      }
    }
  }
}
