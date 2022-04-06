package org.jfantasy.framework.util.common.file;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.tika.Tika;
import org.apache.tika.mime.MimeType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.mime.MimeTypes;
import org.jfantasy.framework.dao.mybatis.keygen.GUIDKeyGenerator;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.NumberUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StreamUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;

@Slf4j
public class FileUtil {

  private static final Tika TIKA = new Tika();
  private static final String REGEXP_START = "[^/]+$";
  public static final String[] UNITS = {"bytes", "KB", "MB", "GB", "TB"};

  private static final String ENCODE = "UTF-8";

  public static String readFile(File file, String charset) {
    String line;
    try (FileInputStream out = new FileInputStream(file)) {
      InputStreamReader read = new InputStreamReader(out, charset);
      BufferedReader reader = new BufferedReader(read);
      StringBuilder buf = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        buf.append(line).append("\n");
      }
      return buf.toString();
    } catch (FileNotFoundException ex) {
      throw new IgnoreException("没有找到文件:" + file.getAbsolutePath(), ex);
    } catch (IOException ex) {
      throw new IgnoreException(ex.getMessage(), ex);
    }
  }

  public static String readFile(String file) {
    return readFile(new File(file));
  }

  public static String readFile(File file) {
    return readFile(file, ENCODE);
  }

  public static String readFile(String file, String charset) {
    return readFile(new File(file), charset);
  }

  public static String readFile(InputStream in) throws IOException {
    return readFile(in, ENCODE);
  }

  public static void readFile(InputStream in, ReadLineCallback readLineCallback)
      throws IOException {
    readFile(in, ENCODE, readLineCallback);
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

    boolean readLine(String line);
  }

  public static String readFile(InputStream in, String charset) throws IOException {
    final StringBuilder html = new StringBuilder();
    readFile(
        in,
        charset,
        line -> {
          html.append(line).append(System.getProperty("line.separator"));
          return true;
        });
    return html.toString();
  }

  public static void writeFile(String content, String file) {

    File f = new File(file).getParentFile();
    if (!f.exists() && !f.mkdirs()) {
      throw new IgnoreException("创建文件" + file + "失败");
    }
    try (FileOutputStream fos = new FileOutputStream(file)) {
      Writer out = new OutputStreamWriter(fos, ENCODE);
      out.flush();
      out.write(content);
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
      throw new IgnoreException(ex.getMessage());
    }
  }

  public static void writeFile(byte[] content, String file) throws IOException {
    createFolder(new File(file).getParentFile());
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(content, 0, content.length);
      fos.flush();
      log.debug("Write File:" + file);
    } catch (IOException ex) {
      log.error(ex.getMessage(), ex);
      throw ex;
    }
  }

  public static File createFolder(String path) {
    return createFolder(new File(path(path).replaceFirst(REGEXP_START, "")));
  }

  public static File createFolder(File file) {
    if (!file.isDirectory()) {
      createFolder(file.getParentFile());
    }
    if (!file.exists() && !file.mkdirs()) {
      log.error("文件: " + file + " > " + file.exists());
    }
    return file;
  }

  public static File createFolder(File file, String folderName) {
    return createFolder(new File(file, folderName));
  }

  private static String path(String pathname) {
    return RegexpUtil.replace(
        pathname, "[" + ("\\".equals(File.separator) ? "\\" : "") + File.separator + "]", "/");
  }

  /**
   * 创建文件并返回
   *
   * @param pathname 文件目录
   * @return {File}
   */
  public static File createFile(String pathname) {
    String tpathname = path(pathname);
    String fileName = RegexpUtil.parseGroup(tpathname, REGEXP_START, 0);
    String parentDir = RegexpUtil.parseGroup(tpathname, REGEXP_START, 0);
    assert parentDir != null;
    return fileName == null
        ? createFolder(tpathname)
        : new File(createFolder(tpathname), parentDir);
  }

  public static File createFile(File parent, String fileName) {
    return new File(createFolder(parent), fileName);
  }

  public static boolean exists(String folderName) {
    return new File(folderName).exists();
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
      throw new IgnoreException("(目录不存在。)folder [" + folder + "]not exist。");
    }
    return folder.listFiles(File::isDirectory);
  }

  public static void compressionGZIP(String oldPath, String newPath) {
    createFolder(
        RegexpUtil.replace(newPath, "(([a-zA-Z0-9]|([(]|[)]|[ ]))+)[.]([a-zA-Z0-9]+)$", ""));
    log.debug(
        "创建文件 ："
            + RegexpUtil.replace(newPath, "(([a-zA-Z0-9]|([(]|[)]|[ ]))+)[.]([a-zA-Z0-9]+)$", "")
            + "|"
            + newPath);
    try (FileInputStream in = new FileInputStream(oldPath);
        FileOutputStream out = new FileOutputStream(newPath)) {
      GZIPOutputStream zipOut = new GZIPOutputStream(out);
      byte[] buf = new byte[1024];
      int num;
      while ((num = in.read(buf)) != -1) {
        zipOut.write(buf, 0, num);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void extractGzip(String fileUrl) {
    try (FileInputStream fin = new FileInputStream(fileUrl);
        FileOutputStream out = new FileOutputStream(RegexpUtil.replace(fileUrl, ".gz$", ""))) {
      GZIPInputStream zipIn = new GZIPInputStream(fin);
      byte[] buf = new byte[1024];
      int num;
      while ((num = zipIn.read(buf, 0, buf.length)) != -1) {
        out.write(buf, 0, num);
      }
    } catch (IOException e) {
      log.error(e.getMessage(), e);
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
      for (int j = 0; j < len; j++) {
        sb.append("0");
      }
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

  public static boolean moveFile(File sourceFile, File targetFile) {
    if (sourceFile.isFile()) {
      return moveOnlyFile(sourceFile, targetFile);
    }
    File[] files = sourceFile.listFiles();
    if (files != null) {
      for (File file : files) {
        String newName = targetFile.getAbsolutePath() + "/" + file.getName();
        moveFile(file, new File(newName));
      }
    }
    if (!sourceFile.delete()) {
      throw new IgnoreException("删除文件" + sourceFile.getAbsolutePath() + "失败");
    }
    return true;
  }

  private static boolean moveOnlyFile(File sourceFile, File targetFile) {
    File parentFile = targetFile.getParentFile();
    if (!parentFile.exists() && !parentFile.mkdirs()) {
      throw new IgnoreException("创建文件" + parentFile.getAbsolutePath() + "失败");
    }
    if (targetFile.exists() && !targetFile.delete()) {
      throw new IgnoreException("删除文件" + targetFile.getAbsolutePath() + "失败");
    }

    if (!sourceFile.renameTo(targetFile)) {
      try {
        copyFile(sourceFile, targetFile);
        if (sourceFile.exists()) {
          log.debug("delete file:" + sourceFile + ":" + sourceFile.delete());
        }
      } catch (IOException e) {
        throw new IgnoreException(e.getMessage(), e);
      }
    }
    return false;
  }

  public static void copyFile(File sourceFile, File targetFile) throws IOException {
    if (sourceFile.isFile()) {
      copyOnlyFile(sourceFile, targetFile);
    } else {
      File[] files = sourceFile.listFiles();
      if (files != null) {
        for (File file : files) {
          String newName = targetFile.getAbsolutePath() + "/" + file.getName();
          copyFile(file, new File(newName));
        }
      }
    }
  }

  private static void copyOnlyFile(File sourceFile, File targetFile) throws IOException {
    log.debug("copy from:" + sourceFile);
    log.debug("copy to:" + targetFile);
    File parentFile = targetFile.getParentFile();
    if (!parentFile.exists() && !parentFile.mkdirs()) {
      throw new IgnoreException("创建文件" + parentFile.getAbsolutePath() + "失败");
    }
    FileInputStream fis = new FileInputStream(sourceFile);
    FileOutputStream fos = new FileOutputStream(targetFile);
    StreamUtil.copyThenClose(fis, fos);
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

  private static void delDir(File dir) {
    File[] files = dir.listFiles();
    if (files != null) {
      for (File eFile : files) {
        delFile(eFile);
      }
    }
    if (!dir.delete()) {
      log.error("删除文件" + dir.getAbsolutePath() + "失败");
    }
  }

  public static void delFile(File file) {
    if (file.exists()) {
      if (file.isFile()) {
        delOnlyFile(file);
      } else {
        delDir(file);
      }
    }
  }

  private static void delOnlyFile(File file) {
    if (file.exists() && !file.delete()) {
      log.error("删除文件" + file.getAbsolutePath() + "失败");
    }
  }

  public static void delFile(String filePath) {
    delFile(new File(filePath));
  }

  public static String tmpdir() {
    return System.getProperty("java.io.tmpdir");
  }

  public static File tmp() {
    return createFile(
        System.getProperty("java.io.tmpdir")
            + File.separator
            + GUIDKeyGenerator.getInstance().getGUID());
  }

  public static File tmp(InputStream in) throws IOException {
    File file = tmp();
    FileOutputStream out = new FileOutputStream(file);
    StreamUtil.copyThenClose(in, out);
    return file;
  }

  public static void replaceInFolder(File file, String oldStr, String newStr) {
    if (file.isDirectory()) {
      File[] files = file.listFiles();
      assert files != null;
      for (File file1 : files) {
        replaceInFolder(file1, oldStr, newStr);
      }
    } else {
      String content = readFile(file);
      if ((file.getName().endsWith(".html")) && (content.contains(oldStr))) {
        writeFile(content, file.getAbsolutePath());
      }
    }
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
      log.warn("Can't detect extension for MIME-type {} {}", mimeType, e);
    }
    return extension;
  }

  public static File writeFile(InputStream in, String filePath) throws IOException {
    File file = createFile(filePath);
    StreamUtil.copyThenClose(in, new FileOutputStream(file));
    return file;
  }

  public void writeFile(OutputStream out, InputStream in) throws IOException {
    StreamUtil.copyThenClose(in, out);
  }

  public static ZipOutputStream compress(String unZipFile, OutputStream zipOut) throws IOException {
    File srcFile = new File(unZipFile);
    DataInputStream dis = new DataInputStream(new FileInputStream(srcFile));
    try {
      ZipOutputStream zos = new ZipOutputStream(zipOut);
      zos.setMethod(ZipOutputStream.DEFLATED);
      ZipEntry ze = new ZipEntry(srcFile.getName());
      zos.putNextEntry(ze);
      DataOutputStream dos = new DataOutputStream(zos);
      StreamUtil.copyThenClose(dis, dos);
      return zos;
    } finally {
      StreamUtil.closeQuietly(dis);
    }
  }

  private static Map<String, Integer> zipHtSizes(File file) {
    Map<String, Integer> htSizes = new HashMap<>();
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

  public static void decompress(File file, UnZipCallBack callBack) {
    Map<String, Integer> htSizes = zipHtSizes(file);

    try (FileInputStream fis = new FileInputStream(file)) {
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

  public interface UnZipCallBack {

    void execute(String fileName, InputStream stream);
  }

  public static long fileSize(long size, String unit) {
    int index = ObjectUtil.indexOf(UNITS, unit);
    return size * Double.valueOf(Math.pow(1024, index)).longValue();
  }

  public static String fileSize(long length) {
    float size = length;
    int i = 0;
    while (size >= 1024 && i < 4) {
      size /= 1024;
      i++;
    }
    if (i < 2) {
      return NumberUtil.format((Math.ceil(size))) + ' ' + UNITS[i];
    } else if (i == 2) {
      return NumberUtil.format(Math.ceil(size * 10) / 10) + ' ' + UNITS[i];
    } else {
      return NumberUtil.format(Math.ceil(size * 100) / 100) + ' ' + UNITS[i];
    }
  }
}
