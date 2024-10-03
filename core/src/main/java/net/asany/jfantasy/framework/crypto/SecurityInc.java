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

public interface SecurityInc {
  String CRYPTO_FORM = "RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING";
  String SIGNATURE_FORM = "MD5WithRSA";
  String ARITHMETIC_RSA = "RSA";
  String ARITHMETIC_DES = "DES";
  String ARITHMETIC_DSA = "DSA";
  int KEY_SIZE = 1024;

  byte[] encrypt(byte[] paramArrayOfByte) throws CryptoException;

  byte[] decrypt(byte[] paramArrayOfByte) throws CryptoException;

  byte[] signature(byte[] paramArrayOfByte) throws CryptoException;

  boolean verify(byte[] paramArrayOfByte1, byte[] paramArrayOfByte2) throws CryptoException;
}
