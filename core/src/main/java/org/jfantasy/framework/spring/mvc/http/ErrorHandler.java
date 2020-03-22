package org.jfantasy.framework.spring.mvc.http;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.error.ErrorUtils;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.spring.mvc.error.SecurityException;
import org.jfantasy.framework.error.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
public class ErrorHandler {

    private static final Log LOG = LogFactory.getLog(ErrorHandler.class);

    protected ApplicationContext applicationContext;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public WebError errorAttributes(Exception exception, HttpServletRequest request, HttpServletResponse response) {
        WebError error = new WebError(request);
        Object state = exception instanceof RestException ? ((RestException) exception).getState() : null;
        if (exception instanceof SecurityException) {
            SecurityException securityException = (SecurityException) exception;
            error.setCode(securityException.getCode());
            error.setMessage(securityException.getMessage());
            response.setStatus(securityException.getStatusCode());
        } else if (exception instanceof ValidationException) {
            ValidationException validationException = (ValidationException) exception;
            error.setCode(validationException.getCode());
            error.setMessage(validationException.getMessage());
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        } else if (exception instanceof RestException) {
            RestException restException = (RestException) exception;
            error.setMessage(restException.getMessage());
            response.setStatus(restException.getStatusCode());
        } else if (exception instanceof MethodArgumentNotValidException) {
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
            ErrorUtils.fill(error, ((MethodArgumentNotValidException) exception));
            response.setStatus(HttpStatus.UNPROCESSABLE_ENTITY.value());
        } else {
            LOG.error(exception.getMessage(), exception);
            error.setMessage(exception.getMessage());
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

