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
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class GZipResponse extends HttpServletResponseWrapper {

  private final GZipStream stream;
  private PrintWriter writer;

  public GZipResponse(HttpServletResponse response) throws IOException {
    super(response);
    this.stream = new GZipStream(response.getOutputStream());
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    return this.stream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (this.writer == null) {
      this.writer =
          new PrintWriter(new OutputStreamWriter(getOutputStream(), getCharacterEncoding()));
    }
    return this.writer;
  }

  public void flush() throws IOException {
    if (this.writer != null) {
      this.writer.flush();
    }
    this.stream.finish();
  }
}
