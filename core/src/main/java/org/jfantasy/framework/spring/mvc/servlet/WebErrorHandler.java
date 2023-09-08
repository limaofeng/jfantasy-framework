package org.jfantasy.framework.spring.mvc.servlet;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.error.ValidationException;
import org.jfantasy.framework.spring.mvc.http.ErrorEvent;
import org.jfantasy.framework.spring.mvc.http.WebError;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

/**
 * @author limaofeng
 */
@Slf4j
@ControllerAdvice
public class WebErrorHandler {

  protected ApplicationContext applicationContext;

  private final WebErrorAttributes webErrorAttributes;

  public WebErrorHandler(WebErrorAttributes webErrorAttributes) {
    this.webErrorAttributes = webErrorAttributes;
  }

  @ExceptionHandler(
      value = {
        ServletException.class,
        ValidationException.class,
        MethodArgumentNotValidException.class,
        MissingServletRequestParameterException.class
      })
  @ResponseBody
  public Map<String, Object> errorAttributes(
      Exception exception, HttpServletRequest request, HttpServletResponse response) {
    String errorCode = ErrorUtils.errorCode(exception);

    WebRequest webRequest = new ServletWebRequest(request, response);

    webErrorAttributes.resolveException(request, response, null, exception);

    request.setAttribute(RequestDispatcher.ERROR_REQUEST_URI, request.getRequestURI());

    if (StringUtil.isNotBlank(errorCode)) {
      request.setAttribute(
          RequestDispatcher.ERROR_STATUS_CODE, Integer.parseInt(errorCode.substring(0, 3)));
    } else {
      request.setAttribute(RequestDispatcher.ERROR_STATUS_CODE, 400);
    }

    Map<String, Object> error =
        webErrorAttributes.getErrorAttributes(webRequest, ErrorAttributeOptions.defaults());

    ErrorUtils.populateErrorAttributesFromException(error, exception);

    response.setStatus((Integer) error.get("status"));

    applicationContext.publishEvent(new ErrorEvent(new WebError(error), null));
    return error;
  }

  @Autowired
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
