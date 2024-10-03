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
package net.asany.jfantasy.framework.util;

import com.github.stuxuhai.jpinyin.DoubleArrayTrie;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.reflect.MethodProxy;

public class PinyinUtils {

  private PinyinUtils() {}

  @SneakyThrows
  public static String getShort(String pinyin) {
    return PinyinHelper.getShortPinyin(pinyin);
  }

  @SneakyThrows
  public static String getAll(String pinyin) {
    return getAll(pinyin, "");
  }

  @SneakyThrows
  public static String getAll(String pinyin, String separator) {
    return PinyinHelper.convertToPinyinString(pinyin, separator, PinyinFormat.WITHOUT_TONE);
  }

  public static void addMutilDict(String key, String value) {
    Map<String, String> data = new HashMap<>();
    data.put(key, value);
    addMutilDict(data);
  }

  public static void addMutilDict(Map<String, String> data) {
    Map<String, String> mutilPinyinTable =
        ClassUtil.getFieldValue(PinyinHelper.class, "MUTIL_PINYIN_TABLE");
    List<String> dict = ClassUtil.getFieldValue(PinyinHelper.class, "dict");
    DoubleArrayTrie doubleArrayTrie =
        ClassUtil.getFieldValue(PinyinHelper.class, "DOUBLE_ARRAY_TRIE");

    MethodProxy methodProxy = ClassUtil.getMethodProxy(DoubleArrayTrie.class, "clear");

    mutilPinyinTable.putAll(data);
    dict.clear();
    assert methodProxy != null;
    methodProxy.invoke(doubleArrayTrie);
    dict.addAll(mutilPinyinTable.keySet());
    Collections.sort(dict);
    doubleArrayTrie.build(dict);
  }
}
