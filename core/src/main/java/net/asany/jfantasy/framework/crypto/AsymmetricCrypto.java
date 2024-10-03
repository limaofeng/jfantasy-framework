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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.*;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

@Slf4j
public class AsymmetricCrypto implements SecurityInc {

  private Cipher ecipher = null;

  private Cipher dcipher = null;

  private Signature sSignature = null;

  private Signature vSignature = null;

  static {
    Security.addProvider(new BouncyCastleProvider());
  }

  public AsymmetricCrypto() throws CryptoException {
    try {
      KeyPair keypair = generatorKeyPair();
      PrivateKey privateKey = keypair.getPrivate();
      PublicKey publicKey = keypair.getPublic();

      if (log.isDebugEnabled()) {

        String builder =
            "====================="
                + "\r\n"
                + privateKey.getAlgorithm()
                + "\r\n"
                + privateKey.getFormat()
                + "\r\n"
                + Arrays.toString(privateKey.getEncoded())
                + "\r\n"
                + publicKey.getAlgorithm()
                + "\r\n"
                + publicKey.getFormat()
                + "\r\n"
                + Arrays.toString(publicKey.getEncoded())
                + "\r\n"
                + "====================="
                + "\r\n";
        log.debug(builder);
      }

      this.ecipher = Cipher.getInstance(CRYPTO_FORM);
      this.ecipher.init(1, publicKey);

      this.dcipher = Cipher.getInstance(CRYPTO_FORM);
      this.dcipher.init(2, privateKey);

      this.sSignature = Signature.getInstance(SIGNATURE_FORM);
      this.sSignature.initSign(privateKey);

      this.vSignature = Signature.getInstance(SIGNATURE_FORM);
      this.vSignature.initVerify(publicKey);
    } catch (Exception e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  private KeyPair generatorKeyPair() throws CryptoException {
    try {
      KeyPairGenerator keyGen = KeyPairGenerator.getInstance(ARITHMETIC_RSA);
      keyGen.initialize(KEY_SIZE);
      return keyGen.genKeyPair();
    } catch (NoSuchAlgorithmException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  @Override
  public byte[] encrypt(byte[] data) throws CryptoException {
    int blockSize = this.ecipher.getBlockSize();
    int outputSize = this.ecipher.getOutputSize(data.length);
    int leavedSize = data.length % blockSize;
    int blocksSize = leavedSize != 0 ? data.length / blockSize + 1 : data.length / blockSize;
    byte[] encrypt = new byte[outputSize * blocksSize];
    int i = 0;
    try {
      while (data.length - i * blockSize > 0) {
        if (data.length - i * blockSize > blockSize) {
          this.ecipher.doFinal(data, i * blockSize, blockSize, encrypt, i * outputSize);
        } else {
          this.ecipher.doFinal(
              data, i * blockSize, data.length - i * blockSize, encrypt, i * outputSize);
        }
        i++;
      }
    } catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
      throw new CryptoException(e.getMessage(), e);
    }

    return encrypt;
  }

  @Override
  public byte[] decrypt(byte[] encryptData) throws CryptoException {

    int blockSize = this.dcipher.getBlockSize();
    ByteArrayOutputStream decrypt = new ByteArrayOutputStream(64);
    int j = 0;

    try {
      while (encryptData.length - j * blockSize > 0) {
        decrypt.write(this.dcipher.doFinal(encryptData, j * blockSize, blockSize));
        j++;
      }
    } catch (IOException | IllegalBlockSizeException | BadPaddingException e) {
      throw new CryptoException(e.getMessage(), e);
    }

    return decrypt.toByteArray();
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
