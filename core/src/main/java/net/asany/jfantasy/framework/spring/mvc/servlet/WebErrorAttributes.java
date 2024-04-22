package net.asany.jfantasy.framework.spring.mvc.servlet;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.error.ErrorUtils;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@Component
public class WebErrorAttributes extends DefaultErrorAttributes {

  @Override
  public Map<String, Object> getErrorAttributes(
      WebRequest request, ErrorAttributeOptions attributeOptions) {
    Map<String, Object> map = super.getErrorAttributes(request, attributeOptions);

    Throwable throwable = getError(request);

    if (throwable == null) {
      return map;
    }

    ErrorUtils.populateErrorAttributesFromException(map, throwable);

    return map;
  }
}
