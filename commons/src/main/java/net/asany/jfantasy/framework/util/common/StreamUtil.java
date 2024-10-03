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
package net.asany.jfantasy.framework.util.common;

import java.io.*;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class StreamUtil {

  private static final int DEFAULT_BUFFER_SIZE = 8192;

  private StreamUtil() {}

  public static void copy(InputStream input, OutputStream output) throws IOException {
    copy(input, output, DEFAULT_BUFFER_SIZE);
  }

  public static void copy(
      InputStream input, OutputStream output, int bufferSize, int start, int end)
      throws IOException {
    byte[] buf = new byte[bufferSize];
    int loadLength = end - start + 1;

    if (start > 0 && input.skip(start) <= 0) {
      throw new IOException(" skip failure");
    }

    int bytesRead = input.read(buf, 0, Math.min(loadLength, bufferSize));
    while (bytesRead != -1 && loadLength > 0) {
      loadLength -= bytesRead;
      output.write(buf, 0, bytesRead);
      output.flush();
      bytesRead = input.read(buf, 0, Math.min(loadLength, bufferSize));
    }
  }

  public static void copy(InputStream input, OutputStream output, int bufferSize)
      throws IOException {
    byte[] buf = new byte[bufferSize];
    int bytesRead = input.read(buf);
    while (bytesRead != -1) {
      output.write(buf, 0, bytesRead);
      output.flush();
      bytesRead = input.read(buf);
    }
  }

  public static void copyThenClose(InputStream input, OutputStream output) throws IOException {
    copy(input, output);
    closeQuietly(input);
    closeQuietly(output);
  }

  public static byte[] getBytes(InputStream input) throws IOException {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    copy(input, result);
    result.close();
    return result.toByteArray();
  }

  public static void closeQuietly(InputStream input) {
    if (input == null) {
      return;
    }
    try {
      input.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void closeQuietly(OutputStream output) {
    if (output == null) {
      return;
    }
    try {
      output.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void closeQuietly(Writer writer) {
    if (writer == null) {
      return;
    }
    try {
      writer.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }

  public static void closeQuietly(Reader in) {
    try {
      in.close();
    } catch (IOException e) {
      log.error(e.getMessage(), e);
    }
  }
}
