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

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.*;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class ThreeDESUtil {

  // 算法名称
  public static final String KEY_ALGORITHM = "desede";
  // 算法名称/加密模式/填充方式
  public static final String CIPHER_ALGORITHM = "desede/CBC/NoPadding";

  private ThreeDESUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * CBC加密
   *
   * @param key 密钥
   * @param keyiv IV
   * @param data 明文
   * @return Base64编码的密文
   * @throws CryptoException
   */
  public static byte[] des3EncodeCBC(byte[] key, byte[] keyiv, byte[] data) throws CryptoException {
    try {
      Security.addProvider(new BouncyCastleProvider());
      Key deskey = keyGenerator(new String(key));
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      IvParameterSpec ips = new IvParameterSpec(keyiv);
      cipher.init(Cipher.ENCRYPT_MODE, deskey, ips);
      return cipher.doFinal(data);
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  /**
   * 生成密钥key对象
   *
   * @param keyStr 密钥字符串
   * @return 密钥对象
   * @throws InvalidKeyException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeySpecException
   */
  private static Key keyGenerator(String keyStr) throws CryptoException {
    try {
      byte[] input = hexString2Bytes(keyStr);
      DESedeKeySpec keySpec = new DESedeKeySpec(input);
      SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(KEY_ALGORITHM);
      return keyFactory.generateSecret(keySpec);
    } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  private static int parse(char c) {
    if (c >= 'a') {
      return (c - 'a' + 10) & 0x0f;
    }
    if (c >= 'A') {
      return (c - 'A' + 10) & 0x0f;
    }
    return (c - '0') & 0x0f;
  }

  // 从十六进制字符串到字节数组转换
  public static byte[] hexString2Bytes(String hexstr) {
    byte[] b = new byte[hexstr.length() / 2];
    int j = 0;
    for (int i = 0; i < b.length; i++) {
      char c0 = hexstr.charAt(j++);
      char c1 = hexstr.charAt(j++);
      b[i] = (byte) ((parse(c0) << 4) | parse(c1));
    }
    return b;
  }

  /**
   * CBC解密
   *
   * @param key 密钥
   * @param keyiv IV
   * @param data Base64编码的密文
   * @return 明文
   * @throws CryptoException
   */
  public static byte[] des3DecodeCBC(byte[] key, byte[] keyiv, byte[] data) throws CryptoException {
    try {
      Key deskey = keyGenerator(new String(key));
      Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM);
      IvParameterSpec ips = new IvParameterSpec(keyiv);
      cipher.init(Cipher.DECRYPT_MODE, deskey, ips);
      return cipher.doFinal(data);
    } catch (NoSuchAlgorithmException
        | NoSuchPaddingException
        | InvalidKeyException
        | InvalidAlgorithmParameterException
        | IllegalBlockSizeException
        | BadPaddingException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }
}
