package org.jfantasy.framework.spring.mvc.reactive;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.google.common.base.Preconditions;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.jfantasy.framework.jackson.BeanPropertyFilter;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.jackson.annotation.BeanFilter;
import org.jfantasy.framework.jackson.annotation.JsonResultFilter;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.reactive.result.method.annotation.ResponseBodyResultHandler;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class WebFluxResponseBodyResultHandler extends ResponseBodyResultHandler {

  public WebFluxResponseBodyResultHandler(
      List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
    super(writers, resolver);
    setOrder(99);
  }

  @Override
  public boolean supports(HandlerResult result) {
    return result.getReturnType().resolve() == Mono.class;
  }

  @Override
  public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
    Object returnValue = result.getReturnValue();
    Preconditions.checkNotNull(returnValue, "response is null!");

    ServerHttpRequest request = exchange.getRequest();
    MethodParameter methodParameter = result.getReturnTypeSource();
    MediaType mediaType = determineMediaType(exchange, methodParameter);

    //    if (request.getMethod() == HttpMethod.GET) {
    //      throw new NotFoundException("查询的对象不存在");
    //    }

    logger.debug("启用自定义 FilterProvider ");

    Mono<MappingJacksonValue> body =
        ((Mono<?>) returnValue)
            .map(
                data -> {
                  MappingJacksonValue mappingJacksonValue = new MappingJacksonValue(data);
                  mappingJacksonValue.setFilters(getFilterProvider(methodParameter));

                  System.out.println(mappingJacksonValue);
                  System.out.println(JSON.serialize(mappingJacksonValue));
                  return mappingJacksonValue;
                });
    return writeBody(body, methodParameter, exchange);
  }

  protected MediaType determineMediaType(ServerWebExchange exchange, MethodParameter returnType) {
    Set<MediaType> producibleMediaTypes = getProducibleMediaTypes(exchange);
    List<MediaType> requestedMediaTypes = exchange.getRequest().getHeaders().getAccept();

    if (!CollectionUtils.isEmpty(requestedMediaTypes)) {
      List<MediaType> resultTypes =
          requestedMediaTypes.stream()
              .filter(
                  producingMediaType ->
                      producibleMediaTypes.stream()
                          .anyMatch(candidate -> candidate.isCompatibleWith(producingMediaType)))
              .collect(Collectors.toList());
      if (!resultTypes.isEmpty()) {
        MimeTypeUtils.sortBySpecificity(resultTypes);
        return resultTypes.get(0);
      }
    }

    if (!producibleMediaTypes.isEmpty()) {
      List<MediaType> resultTypes = new ArrayList<>(producibleMediaTypes);
      MimeTypeUtils.sortBySpecificity(resultTypes);
      return resultTypes.get(0);
    }

    // 默认返回MediaType.APPLICATION_JSON
    return MediaType.APPLICATION_JSON;
  }

  public Set<MediaType> getProducibleMediaTypes(ServerWebExchange exchange) {
    List<MediaType> acceptableMediaTypes = exchange.getRequest().getHeaders().getAccept();
    return new HashSet<>(acceptableMediaTypes);
  }

  private static final ConcurrentMap<String, FilterProvider> PROVIDERS = new ConcurrentHashMap<>();

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

  private static final String DEFAULT_PROVIDER_KEY = "default_provider_key";
}
