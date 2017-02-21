package org.jfantasy.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.autoconfigure.ApiGatewaySettings;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.spring.validation.RESTful;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;

public class DDUtils {

    private static final Log LOG = LogFactory.getLog(DDUtils.class);

    private DDUtils(){
    }

    public static String getName(String key) {
        if (StringUtil.isBlank(key) || !key.contains(":")) {
            return null;
        }
        DataDict dd = getDataDict(key);
        return dd == null ? null : dd.getName();
    }

    public static String getValue(String key) {
        if (StringUtil.isBlank(key) || !key.contains(":")) {
            return null;
        }
        DataDict dd = getDataDict(key);
        return dd == null ? null : dd.getDescription();
    }

    @SuppressWarnings("unchecked")
    public static <T> T getValue(String key, Class<T> clazz) {
        String value = getValue(key);
        if (!ClassUtil.isBasicType(clazz)) {
            throw new ClassCastException(value + " 不能正确的转换为 " + clazz);
        }
        Class laz = clazz.isPrimitive() ? ClassUtil.resolvePrimitiveIfNecessary(clazz) : clazz;
        return value == null ? null : (T) ClassUtil.newInstance(laz, value);
    }

    public static <T> T get(String key, T zero, Class<T> clazz) {
        return ObjectUtil.defaultValue(getValue(key,clazz),zero);
    }

    private static DataDict getDataDict(String id) {
        try {
            ApiGatewaySettings apiGatewaySettings = SpringContextUtil.getBeanByType(ApiGatewaySettings.class);
            assert apiGatewaySettings != null;
            return RESTful.restTemplate.getForObject(apiGatewaySettings.getUrl() + "/system/dds/" + id, DataDict.class);
        }catch (Exception e){
            LOG.error(e.getMessage(),e);
            return null;
        }
    }

}
