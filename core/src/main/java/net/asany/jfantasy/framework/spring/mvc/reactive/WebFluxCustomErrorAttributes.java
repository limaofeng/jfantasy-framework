package net.asany.jfantasy.framework.spring.mvc.reactive;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.ErrorUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;

@Slf4j
@Component
public class WebFluxCustomErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      ServerRequest request, ErrorAttributeOptions attributeOptions) {
    Map<String, Object> errorAttributes = super.getErrorAttributes(request, attributeOptions);

    Throwable throwable = getError(request);

    ErrorUtils.populateErrorAttributesFromException(errorAttributes, throwable);

    return errorAttributes;
  }
}
