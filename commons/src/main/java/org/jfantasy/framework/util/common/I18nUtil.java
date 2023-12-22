package org.jfantasy.framework.util.common;

import java.util.regex.Matcher;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil.AbstractReplaceCallBack;

public class I18nUtil {
  private I18nUtil() {}

  public static String unicode(String text) {
    char[] utfBytes = text.toCharArray();
    StringBuilder unicodeBytes = new StringBuilder();
    for (char utfByte : utfBytes) {
      String hexB = Integer.toHexString(utfByte);
      if (hexB.length() <= 2) {
        hexB = StringUtil.append(hexB, -4, new String[] {"0"});
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
        new AbstractReplaceCallBack() {

          @Override
          public String doReplace(String text, int index, Matcher matcher) {
            return String.valueOf((char) Integer.parseInt(text.substring(2), 16));
          }
        });
  }
}
