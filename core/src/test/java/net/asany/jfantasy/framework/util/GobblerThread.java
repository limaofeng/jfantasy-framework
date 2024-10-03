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
package net.asany.jfantasy.framework.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GobblerThread extends Thread {

  private final StringBuilder result = new StringBuilder();
  private final InputStream is;
  private final String type;

  GobblerThread(InputStream is, String type) {
    this.is = is;
    this.type = type;
  }

  @Override
  public void run() {
    try {
      InputStreamReader isr = new InputStreamReader(is);
      BufferedReader br = new BufferedReader(isr);
      String line;
      while ((line = br.readLine()) != null) {
        result.append(line);
      }
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  public String getResult() {
    try {
      return this.result.toString();
    } finally {
      this.shutoff();
    }
  }

  public void shutoff() {
    //    if (this.isAlive()) {
    //      this.interrupt();
    //    }
  }
}
