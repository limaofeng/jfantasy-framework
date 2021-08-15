package org.jfantasy.framework.crypto;

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
