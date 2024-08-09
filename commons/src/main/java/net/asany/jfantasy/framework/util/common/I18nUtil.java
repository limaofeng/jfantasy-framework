package net.asany.jfantasy.framework.util.common;

import java.util.regex.Matcher;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;

public class I18nUtil {
  private I18nUtil() {}

  public static String unicode(String text) {
    char[] utfBytes = text.toCharArray();
    StringBuilder unicodeBytes = new StringBuilder();
    for (char utfByte : utfBytes) {
      String hexB = Integer.toHexString(utfByte);
      if (hexB.length() <= 2) {
        hexB = StringUtil.addLeft(hexB, 4, "0");
      }
      unicodeBytes.append(
          StringUtil.isChinese(String.valueOf(utfByte)) ? "\\u" + hexB : String.valueOf(utfByte));
    }
    return unicodeBytes.toString();
  }

  public static String decodeUnicode(String text) {
    return RegexpUtil.replace(
        text,
        "\\\\u[0-9a-zA-Z]{4}",
        new RegexpUtil.AbstractReplaceCallBack() {

          @Override
          public String doReplace(String text, int index, Matcher matcher) {
            return String.valueOf((char) Integer.parseInt(text.substring(2), 16));
          }
        });
  }
}
