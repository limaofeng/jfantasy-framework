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

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class AsymmetricCryptoTest {
  @Test
  public void encrypt() throws Exception {
    AsymmetricCrypto cryptor = new AsymmetricCrypto();

    byte[] bytes = "sdfsdfsdf".getBytes();

    byte[] encBytes = cryptor.encrypt(bytes);

    byte[] sin = cryptor.signature(encBytes);

    System.out.println(cryptor.verify(encBytes, sin));

    byte[] denc = cryptor.decrypt(encBytes);

    System.out.println(Arrays.toString(encBytes));
    System.out.println(Arrays.toString(sin));
    System.out.println(Arrays.toString(denc));

    System.out.println(new String(denc));
  }

  @Test
  public void decrypt() throws Exception {}
}
