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
