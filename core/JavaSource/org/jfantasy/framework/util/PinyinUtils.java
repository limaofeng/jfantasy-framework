package org.jfantasy.framework.util;

import com.github.stuxuhai.jpinyin.DoubleArrayTrie;
import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.reflect.MethodProxy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PinyinUtils {

    public static String getShort(String pinyin) throws PinyinException {
        return PinyinHelper.getShortPinyin(pinyin);
    }

    public static String getAll(String pinyin) throws PinyinException {
        return PinyinHelper.convertToPinyinString(pinyin, "", PinyinFormat.WITHOUT_TONE);
    }

    public static void addMutilDict(String key,String value){
        Map<String,String> data = new HashMap<>();
        data.put(key, value);
        addMutilDict(data);
    }

    public static void addMutilDict(Map<String, String> data){
        Map<String, String> mutilPinyinTable = ClassUtil.getFieldValue(PinyinHelper.class,"MUTIL_PINYIN_TABLE");
        List<String> dict = ClassUtil.getFieldValue(PinyinHelper.class,"dict");
        DoubleArrayTrie doubleArrayTrie = ClassUtil.getFieldValue(PinyinHelper.class,"DOUBLE_ARRAY_TRIE");

        MethodProxy methodProxy = ClassUtil.getMethodProxy(DoubleArrayTrie.class,"clear");

        mutilPinyinTable.putAll(data);
        dict.clear();
        methodProxy.invoke(doubleArrayTrie);
        for (String word : mutilPinyinTable.keySet()) {
            dict.add(word);
        }
        Collections.sort(dict);
        doubleArrayTrie.build(dict);
    }

}
