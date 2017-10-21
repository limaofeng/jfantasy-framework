package org.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.jackson.BeanPropertyFilter;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.annotation.BeanFilter;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.jfantasy.framework.spring.mvc.error.NotFoundException;
import org.jfantasy.framework.util.common.ClassUtil;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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

    private static final String DEFAULT_PROVIDER_KEY = "default_provider_key";

    private static final ConcurrentMap<String, FilterProvider> PROVIDERS = new ConcurrentHashMap<>();

    static {
        PROVIDERS.put(DEFAULT_PROVIDER_KEY, new SimpleFilterProvider().setFailOnUnknownId(false));
    }

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> converterType) {
        Class returnType = methodParameter.getMethod().getReturnType();
        return Object.class.isAssignableFrom(returnType);
    }

    @Override
    public Object beforeBodyWrite(Object returnValue, MethodParameter methodParameter, MediaType mediaType, Class<? extends HttpMessageConverter<?>> converterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        if (ClassUtil.isBasicType(methodParameter.getMethod().getReturnType())) {
            return returnValue;
        }
        if (returnValue == null && serverHttpRequest.getMethod() == HttpMethod.GET) {
            throw new NotFoundException("查询的对象不存在");
        }
        if (mediaType.isCompatibleWith(MediaType.APPLICATION_JSON)) {
            LOGGER.debug("启用自定义 FilterProvider ");
            MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(returnValue);
            mappingJacksonValue.setFilters(getFilterProvider(methodParameter));
            return mappingJacksonValue;
        }
        return returnValue;
    }

    public FilterProvider getFilterProvider(JsonResultFilter jsonResultFilter) {
        String key = jsonResultFilter.toString();
        if (PROVIDERS.containsKey(key)) {
            return PROVIDERS.get(key);
        }
        SimpleFilterProvider provider = new SimpleFilterProvider().setFailOnUnknownId(false);
        BeanPropertyFilter propertyFilter = new BeanPropertyFilter();
        for (BeanFilter filter : jsonResultFilter.value()) {
            propertyFilter.includes(filter.type(), filter.includes()).excludes(filter.type(), filter.excludes());
        }
        provider.setDefaultFilter(propertyFilter);
        PROVIDERS.putIfAbsent(key, JSON.permixin(provider));
        return provider;
    }

    private FilterProvider getFilterProvider(MethodParameter methodParameter) {
        JsonResultFilter jsonResultFilter = methodParameter.getMethod().getAnnotation(JsonResultFilter.class);
        if (jsonResultFilter == null) {
            return PROVIDERS.get(DEFAULT_PROVIDER_KEY);
        }
        return getFilterProvider(jsonResultFilter);
    }

}