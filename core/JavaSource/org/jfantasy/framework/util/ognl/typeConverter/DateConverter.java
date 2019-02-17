package org.jfantasy.framework.util.ognl.typeConverter;

import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.DateUtil;
import org.jfantasy.framework.util.common.StringUtil;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Map;

import ognl.DefaultTypeConverter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DateConverter extends DefaultTypeConverter {
    private static final Log LOGGER = LogFactory.getLog(DateConverter.class);

    @Override
    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        if (Date.class.isAssignableFrom(toType)) {
            String dateFormatString;
            try {
                DateFormat dateFormat = (DateFormat) ClassUtil.getParamAnno((Method) member);
                dateFormatString = dateFormat.pattern();
            } catch (Exception e) {
                dateFormatString = "yyyy-MM-dd";
                LOGGER.error(e.getMessage(), e);
            }
            return DateUtil.parse(StringUtil.nullValue(ClassUtil.isArray(value) ? Array.get(value, 0) : value), dateFormatString);
        }
        return super.convertValue(context, target, member, propertyName, value, toType);
    }

}