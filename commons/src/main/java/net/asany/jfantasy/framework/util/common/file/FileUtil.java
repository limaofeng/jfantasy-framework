package net.asany.jfantasy.framework.util.common.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.*;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.StreamUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.error.OperationFailedException;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;

/**
 * 文件工具类
 *
 * @author limaofeng
 */
@Slf4j
public class FileUtil {

  private static final Tika TIKA = new Tika();
  private static final String ZERO_B = "0B";
  private static final String REGEXP_START = "[^/]+$";
  public static final String[] UNITS = {"bytes", "KB", "MB", "GB", "TB"};

  private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

  public static String readString(Path filePath) throws IOException {
    return readString(filePath, DEFAULT_CHARSET);
  }

  public static String readString(Path path, Charset charset) throws IOException {
    return Files.readAllLines(path, charset).stream().reduce("", (a, b) -> a + b + "\n");
  }

  public static String readString(Path path, ReadLineCallback readLineCallback) throws IOException {
    return Files.readAllLines(path, DEFAULT_CHARSET).stream()
        .reduce(
            "",
            (a, b) -> {
              if (readLineCallback.readLine(b)) {
                return a + readLineCallback.readLine(b) + "\n";
              }
              return a;
            });
  }

  public static void readFile(InputStream in, String charset, ReadLineCallback readLineCallback)
      throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, charset))) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (!readLineCallback.readLine(line)) {
          break;
        }
      }
    } finally {
      IOUtils.closeQuietly(in);
    }
  }

  public interface ReadLineCallback {

    /**
     * 读取一行数据
     *
     * @param line 行数据
     * @return 是否继续读取
     */
    boolean readLine(String line);
  }

  public static Path write(Path path, String content) throws IOException {
    if (Files.notExists(path.getParent())) {
      mkdirs(path.getParent());
    }
    return Files.writeString(path, content, DEFAULT_CHARSET, StandardOpenOption.CREATE);
  }

  public static Path write(Path path, byte[] content) throws IOException {
    if (Files.notExists(path.getParent())) {
      mkdirs(path.getParent());
    }
    return Files.write(path, content, StandardOpenOption.CREATE);
  }

  public static Path mkdir(Path path) throws IOException {
    return Files.createDirectory(path);
  }

  public static Path mkdirs(Path path) throws IOException {
    return Files.createDirectories(path);
  }

  /**
   * 创建文件并返回
   *
   * @param path 文件目录
   * @return {File}
   */
  public static Path createFile(Path path) throws IOException {
    Path parentPath = path.getParent();
    if (!Files.exists(parentPath)) {
      Files.createDirectories(parentPath);
    }
    return Files.createFile(path);
  }

  public static boolean exists(String folderName) {
    return Files.exists(Paths.get(folderName));
  }

  public static String getMimeType(File file) {
    try {
      return TIKA.detect(file);
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  public static String getMimeType(InputStream input) {
    try {
      return TIKA.detect(input);
    } catch (IOException e) {
      log.error(e.getMessage());
      return null;
    }
  }

  public static File[] listFolders(String folderName) {
    File folder = new File(folderName);
    if (!folder.exists()) {
      throw new OperationFailedException("(目录不存在。)folder [" + folder + "]not exist。");
    }
    return folder.listFiles(File::isDirectory);
  }

  public static void gzip(Path oldPath, Path newPath) throws IOException {
    Path parentPath = newPath.getParent();
    if (Files.exists(parentPath)) {
      Files.createDirectories(parentPath);
    }
    try (FileInputStream in = new FileInputStream(oldPath.toFile());
        FileOutputStream out = new FileOutputStream(newPath.toFile())) {
      GZIPOutputStream zipOut = new GZIPOutputStream(out);
      StreamUtil.copyThenClose(in, zipOut);
    }
  }

  public static void ungzip(Path fileUrl, Path newPath) throws IOException {
    try (FileInputStream fin = new FileInputStream(fileUrl.toFile());
        FileOutputStream out = new FileOutputStream(newPath.toFile())) {
      GZIPInputStream zipIn = new GZIPInputStream(fin);
      StreamUtil.copyThenClose(zipIn, out);
    }
  }

  public static byte[] gbk2utf8(String chinese) {
    char[] c = chinese.toCharArray();
    byte[] fullByte = new byte[3 * c.length];
    for (int i = 0; i < c.length; i++) {
      int m = c[i];
      String word = Integer.toBinaryString(m);

      StringBuilder sb = new StringBuilder();
      int len = 16 - word.length();
      sb.append("0".repeat(len));
      sb.append(word);
      sb.insert(0, "1110");
      sb.insert(8, "10");
      sb.insert(16, "10");

      String s1 = sb.substring(0, 8);
      String s2 = sb.substring(8, 16);
      String s3 = sb.substring(16);

      byte b0 = Integer.valueOf(s1, 2).byteValue();
      byte b1 = Integer.valueOf(s2, 2).byteValue();
      byte b2 = Integer.valueOf(s3, 2).byteValue();
      byte[] bf = new byte[3];
      bf[0] = b0;
      fullByte[i * 3] = bf[0];
      bf[1] = b1;
      fullByte[i * 3 + 1] = bf[1];
      bf[2] = b2;
      fullByte[i * 3 + 2] = bf[2];
    }

    return fullByte;
  }

  /**
   * 移动文件或者文件夹
   *
   * @param source 源文件
   * @param target 目标文件
   * @throws IOException 异常
   */
  public static void mv(Path source, Path target) throws IOException {
    if (!Files.isDirectory(source)) {
      Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    } else {
      if (!Files.exists(target)) {
        Files.createDirectories(target);
      }
      try (Stream<Path> stream = Files.list(source)) {
        stream.forEach(
            path -> {
              try {
                mv(path, target.resolve(source.relativize(path)));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
      }
    }
  }

  public static void copy(Path source, Path target) throws IOException {
    if (!Files.isDirectory(source)) {
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    } else {
      if (!Files.exists(target)) {
        Files.createDirectories(target);
      }
      try (Stream<Path> stream = Files.list(source)) {
        stream.forEach(
            path -> {
              try {
                copy(path, target.resolve(source.relativize(path)));
              } catch (IOException e) {
                throw new RuntimeException(e);
              }
            });
      }
    }
  }

  public static URL generate(URL url, File file) throws IOException {
    HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
    BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
    StringBuilder buf = new StringBuilder();
    String line;
    while ((line = in.readLine()) != null) {
      buf.append("\n").append(line);
    }
    StreamUtil.closeQuietly(in);
    FileWriter fw = new FileWriter(file);
    try {
      fw.write(buf.toString());
      fw.flush();
    } finally {
      StreamUtil.closeQuietly(fw);
    }
    return url;
  }

  /**
   * 删除文件或者文件夹
   *
   * @param path 文件或者文件夹路径
   * @throws IOException 异常
   */
  public static void rm(Path path) throws IOException {
    Files.delete(path);
  }

  /**
   * 删除文件夹
   *
   * @param path 文件或者文件夹路径
   * @param force 强制删除 类似 rm -rf
   * @throws IOException 异常
   */
  public static void rm(Path path, boolean force) throws IOException {
    if (!Files.isDirectory(path)) {
      Files.delete(path);
      return;
    }
    Files.walkFileTree(
        path,
        new SimpleFileVisitor<>() {
          @Override
          public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
              throws IOException {
            Files.delete(file);
            return FileVisitResult.CONTINUE;
          }

          @Override
          public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
            if (force) {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }
            return FileVisitResult.SKIP_SUBTREE;
          }
        });
  }

  public static String tmpdir() {
    return System.getProperty("java.io.tmpdir");
  }

  public static Path tmp() {
    return Paths.get(System.getProperty("java.io.tmpdir") + File.separator + StringUtil.guid());
  }

  public static File tmp(InputStream in) throws IOException {
    File file = tmp().toFile();
    FileOutputStream out = new FileOutputStream(file);
    StreamUtil.copyThenClose(in, out);
    return file;
  }

  public static Date lastModified(String filePath) {
    File file = new File(filePath);
    return new Date(file.lastModified());
  }

  public static Date lastModified(File file) {
    return new Date(file.lastModified());
  }

  public static String getExtension(String mimeType) {
    String extension = ".bin";
    try {
      MimeTypes allTypes = MimeTypes.getDefaultMimeTypes();
      MimeType _mimeType = allTypes.forName(mimeType);
      extension = _mimeType.getExtension();
    } catch (MimeTypeException e) {
      log.warn("Can't detect extension for MIME-type {} {}", mimeType, e.getMessage());
    }
    return extension;
  }

  public static void zip(Path source, Path target) throws IOException {
    zip(source, Files.newOutputStream(target));
  }

  public static void zip(Path source, OutputStream output) throws IOException {
    try (ZipOutputStream zos = new ZipOutputStream(output)) {
      if (!Files.isDirectory(source)) {
        throw new IllegalArgumentException(source.getFileName() + " is not a directory");
      }
      Files.walkFileTree(
          source,
          new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              zip(Files.newInputStream(file), source.relativize(file).toString(), zos);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                throws IOException {
              return FileVisitResult.CONTINUE;
            }
          });
    }
  }

  public static void zip(
      List<String> filenames, OutputStream output, Function<String, InputStream> isf)
      throws IOException {
    try (ZipOutputStream zos = new ZipOutputStream(output)) {
      for (String filename : filenames) {
        InputStream fis = isf.apply(filename);
        zip(fis, filename, zos);
      }
    }
  }

  private static void zip(InputStream input, String filename, ZipOutputStream zos)
      throws IOException {
    ZipEntry zipEntry = new ZipEntry(filename);
    zos.putNextEntry(zipEntry);
    StreamUtil.copyThenClose(input, zos);
  }

  private static Map<String, Integer> zipHtSizes(File file) {
    Map<String, Integer> htSizes = new HashMap<>(1);
    try (ZipFile zf = new ZipFile(file)) {
      Enumeration<? extends ZipEntry> e = zf.entries();
      while (e.hasMoreElements()) {
        ZipEntry ze = e.nextElement();
        htSizes.put(ze.getName(), (int) ze.getSize());
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
    return htSizes;
  }

  public static void unzip(Path path, UnZipCallBack callBack) {
    Map<String, Integer> htSizes = zipHtSizes(path.toFile());
    try (FileInputStream fis = new FileInputStream(path.toFile())) {
      BufferedInputStream bis = new BufferedInputStream(fis);
      ZipInputStream zis = new ZipInputStream(bis);
      ZipEntry ze;
      while ((ze = zis.getNextEntry()) != null) {
        if (ze.isDirectory()) {
          continue;
        }
        int size = (int) ze.getSize();
        if (size == -1) {
          size = htSizes.get(ze.getName());
        }
        byte[] b = new byte[size];
        int rb = 0;
        int chunk;
        while ((size - rb) > 0) {
          chunk = zis.read(b, rb, size - rb);
          if (chunk == -1) {
            break;
          }
          rb += chunk;
        }
        callBack.execute(ze.getName(), new ByteArrayInputStream(b));
      }
      zis.closeEntry();
    } catch (NullPointerException | IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  /** 解压缩回调 */
  public interface UnZipCallBack {

    /**
     * 每个文件的回调
     *
     * @param fileName 文件名
     * @param stream 文件流
     */
    void execute(String fileName, InputStream stream);
  }

  public static String size(Path path) throws IOException {
    return bytesToSize(Files.size(path));
  }

  public static String bytesToSize(long bytes) {
    if (bytes <= 0) {
      return ZERO_B;
    }
    int digitGroups = (int) (Math.log10(bytes) / Math.log10(1024));
    return new DecimalFormat("#,##0.#").format(bytes / Math.pow(1024, digitGroups))
        + " "
        + UNITS[digitGroups];
  }
}
