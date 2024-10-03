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
package net.asany.jfantasy.framework.spring.mvc.servlet;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.FilteredMixinHolder;
import net.asany.jfantasy.framework.spring.mvc.error.NotFoundException;
import net.asany.jfantasy.framework.util.common.ClassUtil;
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
      mappingJacksonValue.setFilters(FilteredMixinHolder.getFilterProvider(methodParameter));
      return mappingJacksonValue;
    }
    return returnValue;
  }
}
