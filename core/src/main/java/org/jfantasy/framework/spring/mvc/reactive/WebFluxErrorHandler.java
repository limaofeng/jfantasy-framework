package org.jfantasy.framework.spring.mvc.reactive;

import java.util.Map;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

@Component
@Order(-2)
public class WebFluxErrorHandler extends AbstractErrorWebExceptionHandler {
  public WebFluxErrorHandler(
      WebFluxCustomErrorAttributes g,
      ApplicationContext applicationContext,
      ServerCodecConfigurer serverCodecConfigurer) {
    super(g, new ResourceProperties(), applicationContext);
    super.setMessageWriters(serverCodecConfigurer.getWriters());
    super.setMessageReaders(serverCodecConfigurer.getReaders());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(
      final ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {
    final Map<String, Object> errorPropertiesMap =
        getErrorAttributes(request, ErrorAttributeOptions.defaults());
    int statusCode =
        Integer.parseInt(String.valueOf(errorPropertiesMap.getOrDefault("status", "400")));
    return ServerResponse.status(HttpStatus.valueOf(statusCode))
        .contentType(MediaType.APPLICATION_JSON)
        .body(BodyInserters.fromValue(errorPropertiesMap));
  }

  private static class ResourceProperties extends WebProperties.Resources {}
}
