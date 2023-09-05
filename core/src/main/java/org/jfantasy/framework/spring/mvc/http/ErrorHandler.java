package org.jfantasy.framework.spring.mvc.http;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.error.ValidationException;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.SecurityException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/** @author limaofeng */
@Slf4j
@ControllerAdvice
public class ErrorHandler {

  protected ApplicationContext applicationContext;

  @ExceptionHandler(
      value = {
        ValidationException.class,
        MethodArgumentNotValidException.class,
        MissingServletRequestParameterException.class
      })
  @ResponseBody
  public WebError errorAttributes(
      Exception exception, HttpServletRequest request, HttpServletResponse response) {
    WebError error = new WebError(request);
    Object state =
        exception instanceof RestException ? ((RestException) exception).getState() : null;
    ErrorUtils.fill(error, exception);
    if (exception instanceof SecurityException) {
      SecurityException securityException = (SecurityException) exception;
      error.setCode(securityException.getCode());
      response.setStatus(securityException.getStatusCode());
    } else if (exception instanceof ValidationException) {
      ErrorUtils.fill(error, (ValidationException) exception);
      response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
    } else if (exception instanceof MethodArgumentNotValidException) {
      ErrorUtils.fill(error, ((MethodArgumentNotValidException) exception));
      response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
    } else if (exception instanceof MissingServletRequestParameterException) {
      ErrorUtils.fill(error, ((MissingServletRequestParameterException) exception));
      response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
    } else {
      log.error(exception.getMessage(), exception);
      response.setStatus(HttpStatus.BAD_REQUEST.value());
    }
    applicationContext.publishEvent(new ErrorEvent(error, state));
    return error;
  }

  @Autowired
  public void setApplicationContext(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }
}
