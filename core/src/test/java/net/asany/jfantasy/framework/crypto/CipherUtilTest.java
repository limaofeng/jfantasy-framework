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

public class CipherUtilTest {

  @Test
  public void generatePassword() throws Exception {
    String pwd1 = "123";
    String pwd2 = "";
    System.out.println("未加密的密码:" + pwd1);

    pwd2 = CipherUtil.generatePassword(pwd1);
    System.out.println("加密后的密码:" + pwd2);

    System.out.print("验证密码是否下确:");
    if (CipherUtil.validatePassword(pwd2, pwd1)) {
      System.out.println("正确");
    } else {
      System.out.println("错误");
    }
  }

  @Test
  public void validatePassword() throws Exception {}
}
