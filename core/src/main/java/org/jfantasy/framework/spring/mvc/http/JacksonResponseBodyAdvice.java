package org.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.jackson.BeanPropertyFilter;
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
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 支持 REST 接口的响应头设置 <br>
 *
 * @author limaofeng
 */
@Order(1)
@Slf4j
@ControllerAdvice
public class JacksonResponseBodyAdvice implements ResponseBodyAdvice<Object> {

  private static final String DEFAULT_PROVIDER_KEY = "default_provider_key";

  private static final ConcurrentMap<String, FilterProvider> PROVIDERS = new ConcurrentHashMap<>();

  static {
    PROVIDERS.put(DEFAULT_PROVIDER_KEY, new SimpleFilterProvider().setFailOnUnknownId(false));
  }

  @Override
  public boolean supports(
      MethodParameter methodParameter,
      @Nullable Class<? extends HttpMessageConverter<?>> converterType) {
    Class<?> returnType = Objects.requireNonNull(methodParameter.getMethod()).getReturnType();
    return Object.class.isAssignableFrom(returnType);
  }

  @Override
  public Object beforeBodyWrite(
      Object returnValue,
      @Nullable MethodParameter methodParameter,
      @Nullable MediaType mediaType,
      @Nullable Class<? extends HttpMessageConverter<?>> converterType,
      @Nullable ServerHttpRequest serverHttpRequest,
      @Nullable ServerHttpResponse serverHttpResponse) {
    if (ClassUtil.isBasicType(
        Objects.requireNonNull(Objects.requireNonNull(methodParameter).getMethod())
            .getReturnType())) {
      return returnValue;
    }
    if (returnValue == null
        && Objects.requireNonNull(serverHttpRequest).getMethod() == HttpMethod.GET) {
      throw new NotFoundException("查询的对象不存在");
    }
    if (Objects.requireNonNull(mediaType).isCompatibleWith(MediaType.APPLICATION_JSON)) {
      log.debug("启用自定义 FilterProvider ");
      if (returnValue == null) {
        return null;
      }
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
      if (filter.type().isArray()) {
        continue;
      }
      propertyFilter.mixin(filter.type()).includes(filter.includes()).excludes(filter.excludes());
    }
    provider.setDefaultFilter(propertyFilter);
    PROVIDERS.putIfAbsent(key, provider);
    return provider;
  }

  private FilterProvider getFilterProvider(MethodParameter methodParameter) {
    JsonResultFilter jsonResultFilter =
        Objects.requireNonNull(methodParameter.getMethod()).getAnnotation(JsonResultFilter.class);
    if (jsonResultFilter == null) {
      return PROVIDERS.get(DEFAULT_PROVIDER_KEY);
    }
    return getFilterProvider(jsonResultFilter);
  }
}
