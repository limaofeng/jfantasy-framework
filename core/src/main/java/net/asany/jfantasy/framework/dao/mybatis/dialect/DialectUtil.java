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
package net.asany.jfantasy.framework.dao.mybatis.dialect;

import java.util.regex.Matcher;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;

public class DialectUtil {

  private DialectUtil() {}

  /** 关键字数组 */
  private static final String[] keywords =
      new String[] {
        "select", "top", "count", "from", "order", "by", "asc", "desc", "group", "where", "and"
      };

  private static String keywordRegexp = null;

  static {
    StringBuilder buffer = new StringBuilder("^(");
    for (int i = 0; i < keywords.length; i++) {
      String keyword = keywords[i];
      buffer.append("(").append(keyword).append(")");
      if (i != keywords.length - 1) {
        buffer.append("|");
      }
    }
    buffer.append(")$");
    keywordRegexp = buffer.toString();
  }

  private static final RegexpUtil.ReplaceCallBack antonymReplaceCallBack =
      new RegexpUtil.AbstractReplaceCallBack() {

        @Override
        public String doReplace(String text, int index, Matcher matcher) {
          if (RegexpUtil.find(text.toLowerCase(), "^((asc)|(desc))$")) {
            return "asc".equalsIgnoreCase(text) ? "desc" : "asc";
          }
          return text;
        }
      };

  /**
   * sql 排序取反
   *
   * @param over over
   * @return string
   */
  public static String antonymOver(String over) {
    return RegexpUtil.replace(over, "[a-zA-Z0-9_]{1,}", antonymReplaceCallBack);
  }

  /**
   * sql 美化 查询关键字大写
   *
   * @param sql sql
   * @return sql
   */
  public static String pretty(String sql) {
    return RegexpUtil.replace(
        sql,
        "[a-zA-Z0-9_]{1,}",
        new RegexpUtil.AbstractReplaceCallBack() {

          @Override
          public String doReplace(String text, int index, Matcher matcher) {
            if (RegexpUtil.find(text.toLowerCase(), keywordRegexp)) {
              return text.toUpperCase();
            }
            return text;
          }
        });
  }
}
