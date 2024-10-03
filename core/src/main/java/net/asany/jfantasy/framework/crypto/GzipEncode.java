/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.crypto;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.StreamUtil;
import org.apache.commons.io.output.ByteArrayOutputStream;

@Slf4j
public class GzipEncode {

  public String encode(String sb) {
    return sb;
  }

  public static void gzip(InputStream in, OutputStream out) throws IOException {
    try (GZIPOutputStream gout = new GZIPOutputStream(out)) {
      StreamUtil.copy(in, gout);
    } finally {
      StreamUtil.closeQuietly(in);
      StreamUtil.closeQuietly(out);
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
      log.error(e.getMessage(), e);
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
      log.error(e.getMessage(), e);
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
