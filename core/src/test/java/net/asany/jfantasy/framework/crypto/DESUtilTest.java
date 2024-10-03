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

import org.junit.jupiter.api.Test;

public class DESUtilTest {
  @Test
  public void encrypt() throws Exception {
    String source = "市规土局所属事业单位招聘20名工作人员"; // "amigoxie";
    System.out.println("原文: " + source);
    String key = "A1B2C3D4E5F60708";
    String encryptData = DESUtil.encrypt(source, key);
    System.out.println("加密后: " + encryptData);
    String decryptData = DESUtil.decrypt(encryptData, key);
    System.out.println("解密后: " + decryptData);
  }

  @Test
  public void decrypt() throws Exception {
    DESPlus desPlus = new DESPlus("hooluesoft");
    System.out.println(desPlus.decrypt("76e7d85003fc8963592483e28a53c290"));
    System.out.println(desPlus.encrypt("15921884771"));
  }
}
