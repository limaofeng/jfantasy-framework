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
import javax.crypto.*;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

public class SymmetricCrypto implements SecurityInc {

  private Cipher ecipher = null;

  private Cipher dcipher = null;

  private Signature sSignature = null;

  private Signature vSignature = null;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public SymmetricCrypto() throws CryptoException {
    try {
      SecretKey secretKey = KeyGenerator.getInstance("DES").generateKey();

      this.ecipher = Cipher.getInstance("DES");
      this.ecipher.init(1, secretKey);

      this.dcipher = Cipher.getInstance("DES");
      this.dcipher.init(2, secretKey);

      KeyPair keypair = generatorKeyPair();
      PrivateKey privateKey = keypair.getPrivate();
      PublicKey publicKey = keypair.getPublic();

      this.sSignature = Signature.getInstance("DSA");
      this.sSignature.initSign(privateKey);

      this.vSignature = Signature.getInstance("DSA");
      this.vSignature.initVerify(publicKey);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  private KeyPair generatorKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
    keyGen.initialize(1024, new SecureRandom());
    return keyGen.generateKeyPair();
  }

  @Override
  public byte[] encrypt(byte[] data) throws CryptoException {
    try {
      return this.ecipher.doFinal(data);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  @Override
  public byte[] decrypt(byte[] data) throws CryptoException {
    try {
      return this.dcipher.doFinal(data);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  @Override
  public byte[] signature(byte[] data) throws CryptoException {
    try {
      this.sSignature.update(data);
      return this.sSignature.sign();
    } catch (SignatureException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  @Override
  public boolean verify(byte[] buffer, byte[] signData) throws CryptoException {
    try {
      this.vSignature.update(buffer);
      return this.vSignature.verify(signData);
    } catch (SignatureException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }
}
