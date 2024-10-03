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
package net.asany.jfantasy.framework.web.util.gzip;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class GZipStream extends ServletOutputStream {

  private final GZIPOutputStream zipStream;

  public GZipStream(OutputStream out) throws IOException {
    this.zipStream = new GZIPOutputStream(out);
  }

  @Override
  public void flush() throws IOException {
    this.zipStream.flush();
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    this.zipStream.write(b, off, len);
  }

  @Override
  public void write(byte[] b) throws IOException {
    this.zipStream.write(b);
  }

  @Override
  public void write(int arg0) throws IOException {
    this.zipStream.write(arg0);
  }

  public void finish() throws IOException {
    this.zipStream.finish();
  }

  @Override
  public void close() throws IOException {
    this.zipStream.close();
  }

  @Override
  public boolean isReady() {
    return false;
  }

  @Override
  public void setWriteListener(WriteListener writeListener) {}
}
