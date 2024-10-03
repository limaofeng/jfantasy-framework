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
