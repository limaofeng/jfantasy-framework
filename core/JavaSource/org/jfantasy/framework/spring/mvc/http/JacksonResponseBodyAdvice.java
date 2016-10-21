package org.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.ThreadJacksonMixInHolder;
import org.jfantasy.framework.jackson.annotation.AllowProperty;
import org.jfantasy.framework.jackson.annotation.IgnoreProperty;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.*;

/**
 * 支持 REST 接口的响应头设置 <br/>
 * 1、X-Expend-Fields   设置需要返回的字段、在原返回字段中添加额外的字段 <br/>
 * 2、X-Result-Fields   完全由调用端控制返回的字段数量 <br/>
 * 例：X-Result-Fields：username,nickName <br/>
 */
@Order(1)
@ControllerAdvice
public class JacksonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private static final Log LOGGER = LogFactory.getLog(JacksonResponseBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        Class returnType = methodParameter.getMethod().getReturnType();
        return Object.class.isAssignableFrom(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object returnValue, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (mediaType.isCompatibleWith(MediaTypes.HAL_JSON) || mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            if (isFilterProvider(methodParameter)) {
                return returnValue;
            }
            LOGGER.debug("启用自定义 FilterProvider ");
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(returnValue);
            mappingJacksonValue.setFilters(getFilterProvider(methodParameter));
            return mappingJacksonValue;
        }
        return returnValue;
    }

    private FilterProvider getFilterProvider(JsonResultFilter jsonResultFilter) {
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);

        AllowProperty[] allowProperties = jsonResultFilter.allow();
        IgnoreProperty[] ignoreProperties = jsonResultFilter.ignore();


        Map<String, Set<String>> ignorePropertyNames = new HashMap<>();
        Map<String, Set<String>> allowPropertyNames = new HashMap<>();

        for (AllowProperty property : allowProperties) {
            Class<?> target = property.pojo();
            String[] names = StringUtil.tokenizeToStringArray(property.name());

            ThreadJacksonMixInHolder.MixInSource mixInSource = ThreadJacksonMixInHolder.createMixInSource(target);
            if (!allowPropertyNames.containsKey(mixInSource.getFilterName())) {
                allowPropertyNames.put(mixInSource.getFilterName(), new HashSet<>(Arrays.asList(names)));
            } else {
                allowPropertyNames.get(mixInSource.getFilterName()).addAll(Arrays.asList(names));
            }
        }
        for (IgnoreProperty property : ignoreProperties) {
            Class<?> target = property.pojo();
            String[] names = StringUtil.tokenizeToStringArray(property.name());

            ThreadJacksonMixInHolder.MixInSource mixInSource = ThreadJacksonMixInHolder.createMixInSource(target);
            if (!ignorePropertyNames.containsKey(mixInSource.getFilterName())) {
                ignorePropertyNames.put(mixInSource.getFilterName(), new HashSet<>(Arrays.asList(names)));
            } else {
                ignorePropertyNames.get(mixInSource.getFilterName()).addAll(Arrays.asList(names));
            }
        }

        for (Map.Entry<String, Set<String>> entry : allowPropertyNames.entrySet()) {
            provider.addFilter(entry.getKey(), SimpleBeanPropertyFilter.filterOutAllExcept(entry.getValue()));
        }
        ignorePropertyNames.entrySet().stream().filter(entry -> !allowPropertyNames.containsKey(entry.getKey())).forEach(entry -> provider.addFilter(entry.getKey(), SimpleBeanPropertyFilter.serializeAllExcept(entry.getValue())));
        LOGGER.debug(provider);
        return provider;
    }

    private boolean isFilterProvider(MethodParameter methodParameter) {
        return methodParameter.getMethod().getAnnotation(JsonResultFilter.class) != null;
    }

    private FilterProvider getFilterProvider(MethodParameter methodParameter) {
        JsonResultFilter jsonResultFilter = methodParameter.getMethod().getAnnotation(JsonResultFilter.class);
        if (jsonResultFilter == null) {
            return null;
        }
        return getFilterProvider(jsonResultFilter);
    }

}