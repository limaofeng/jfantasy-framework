package org.jfantasy.system.converter;

import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.jfantasy.system.bean.Dict;
import org.jfantasy.system.service.DataDictionaryService;
import ognl.DefaultTypeConverter;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用于将 DataDictionary 类作为动态属性时的转换方法。
 */
public class DataDictionaryTypeConverter extends DefaultTypeConverter {

    @Autowired
    private DataDictionaryService dataDictionaryService;

    @Transactional
    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (toType == Dict.class) {
            String values = StringUtil.nullValue(ClassUtil.isArray(value) ? Array.get(value, 0) : value);
            return dataDictionaryService.get(values);
        } else if (toType == Dict[].class) {
            String values = StringUtil.nullValue(ClassUtil.isArray(value) ? Array.get(value, 0) : value);
            if (StringUtil.isBlank(values)) {
                return new Dict[0];
            }
            String[] datas = RegexpUtil.split(values, ",");
            if (datas.length == 0) {
                return new Dict[0];
            }
            List<Dict> dataDicts = new ArrayList<Dict>();
            for (String data : datas) {
                Dict dict = dataDictionaryService.get(data);
                if (dict == null) {
                    continue;
                }
                dataDicts.add(dict);
            }
            return dataDicts.toArray(new Dict[dataDicts.size()]);
        } else if (value instanceof Dict && toType == String.class) {
            return OgnlUtil.getInstance().getValue("key", value).toString();
        } else if (value instanceof Dict[] && toType == String.class) {
            StringBuilder stringBuilder = new StringBuilder();
            for (Dict dict : (Dict[]) value) {
                stringBuilder.append(dict.getKey()).append(",");
            }
            return stringBuilder.toString();
        }
        return super.convertValue(context, target, member, propertyName, value, toType);
    }

}
