package net.asany.jfantasy.framework.crypto;

import java.security.MessageDigest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CipherUtil {

  private static final String[] hexDigits = {
    "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"
  };

  private CipherUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static String generatePassword(String inputString) {
    return encodeByMD5(inputString);
  }

  public static boolean validatePassword(String password, String inputString) {
    return password.equals(encodeByMD5(inputString));
  }

  public static String returnEncodeByMde(String originString) {
    return encodeByMD5(originString);
  }

  private static String encodeByMD5(String originString) {
    if (originString == null) {
      return null;
    }
    try {
      MessageDigest md = MessageDigest.getInstance("MD5");
      byte[] results = md.digest(originString.getBytes());
      String resultString = byteArrayToHexString(results);
      return resultString.toUpperCase();
    } catch (Exception ex) {
      log.error(ex.getMessage(), ex);
      return null;
    }
  }

  private static String byteArrayToHexString(byte[] b) {
    StringBuilder resultSb = new StringBuilder();
    for (byte value : b) {
      resultSb.append(byteToHexString(value));
    }
    return resultSb.toString();
  }

  private static String byteToHexString(byte b) {
    int n = b;
    if (n < 0) {
      n += 256;
    }
    int d1 = n / 16;
    int d2 = n % 16;
    return hexDigits[d1] + hexDigits[d2];
  }
}
