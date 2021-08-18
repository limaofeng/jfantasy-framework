package org.jfantasy.framework.crypto;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.StringUtil;

public class DESPlus {

  private static final Log LOG = LogFactory.getLog(DESPlus.class);

  private static final String DEFAULT_KEY = "jfantasy.org";
  private static final String CIPHER_TYPE = "DES";
  private Cipher encryptCipher = null;
  private Cipher decryptCipher = null;

  public DESPlus() {
    this(DEFAULT_KEY);
  }

  public DESPlus(String strKey) {
    // Security.addProvider(new SunJCE());
    try {
      Key key = getKey(strKey.getBytes());
      this.encryptCipher = Cipher.getInstance(CIPHER_TYPE);
      this.encryptCipher.init(1, key);

      this.decryptCipher = Cipher.getInstance(CIPHER_TYPE);
      this.decryptCipher.init(2, key);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
      LOG.error(e.getMessage(), e);
    }
  }

  private static String byteArr2HexStr(byte[] arrB) {
    int iLen = arrB.length;

    StringBuilder sb = new StringBuilder(iLen * 2);
    for (byte anArrB : arrB) {
      int intTmp = anArrB;

      while (intTmp < 0) {
        intTmp += 256;
      }

      if (intTmp < 16) {
        sb.append("0");
      }
      sb.append(Integer.toString(intTmp, 16));
    }
    return sb.toString();
  }

  private static byte[] hexStr2ByteArr(String strIn) {
    byte[] arrB = strIn.getBytes();
    int iLen = arrB.length;

    byte[] arrOut = new byte[iLen / 2];
    for (int i = 0; i < iLen; i += 2) {
      String strTmp = new String(arrB, i, 2);
      arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
    }
    return arrOut;
  }

  public byte[] encrypt(byte[] arrB) throws CryptoException {
    try {
      return this.encryptCipher.doFinal(arrB);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  public String encrypt(String strIn) throws CryptoException {
    if (StringUtil.isBlank(strIn)) {
      return "";
    }
    return byteArr2HexStr(encrypt(strIn.getBytes()));
  }

  public byte[] decrypt(byte[] arrB) throws CryptoException {
    try {
      return this.decryptCipher.doFinal(arrB);
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      throw new CryptoException(e.getMessage(), e);
    }
  }

  public String decrypt(String strIn) throws CryptoException {
    return new String(decrypt(hexStr2ByteArr(strIn)));
  }

  private Key getKey(byte[] arrBTmp) {
    byte[] arrB = new byte[8];
    for (int i = 0; (i < arrBTmp.length) && (i < arrB.length); i++) {
      arrB[i] = arrBTmp[i];
    }
    return new SecretKeySpec(arrB, "DES");
  }
}
