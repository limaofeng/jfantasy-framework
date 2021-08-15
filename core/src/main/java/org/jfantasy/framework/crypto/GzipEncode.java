package org.jfantasy.framework.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.StreamUtil;

public class GzipEncode {

  private static final Log LOGGER = LogFactory.getLog(GzipEncode.class);

  public String encode(String sb) {
    return sb;
  }

  public static void gzip(InputStream in, OutputStream out) {
    try {
      GZIPOutputStream gzout = new GZIPOutputStream(out);
      byte[] buf = new byte[1024];
      int num;
      while ((num = in.read(buf)) != -1) {
        gzout.write(buf, 0, num);
      }
      gzout.close();
      out.close();
      in.close();
    } catch (IOException e) {
      System.out.println(e);
    }
  }

  public static byte[] gzip(String buffer) throws IOException {
    ByteArrayOutputStream o = null;
    GZIPOutputStream gzout = null;
    try {
      o = new ByteArrayOutputStream();
      gzout = new GZIPOutputStream(o);
      gzout.write(buffer.getBytes());
      gzout.finish();
      return o.toByteArray();
    } catch (IOException e) {
      LOGGER.error(e);
      throw e;
    } finally {
      StreamUtil.closeQuietly(gzout);
      StreamUtil.closeQuietly(o);
    }
  }

  public static byte[] jUnZip(byte[] buffer) throws IOException {
    try {
      byte[] buf = new byte[8192];
      ByteArrayInputStream i = new ByteArrayInputStream(buffer);
      GZIPInputStream gzin = new GZIPInputStream(i);
      int size = gzin.read(buf);
      i.close();
      gzin.close();
      byte[] b = new byte[size];
      System.arraycopy(buf, 0, b, 0, size);
      return b;
    } catch (IOException e) {
      LOGGER.error(e);
      throw e;
    }
  }

  public static String KL(String inStr) {
    char[] a = inStr.toCharArray();
    for (int i = 0; i < a.length; i++) {
      a[i] = (char) (a[i] ^ 0x74);
    }
    String s = new String(a);
    return s;
  }

  public static String JM(String inStr) {
    char[] a = inStr.toCharArray();
    for (int i = 0; i < a.length; i++) {
      a[i] = (char) (a[i] ^ 0x74);
    }
    String k = new String(a);
    return k;
  }
}
