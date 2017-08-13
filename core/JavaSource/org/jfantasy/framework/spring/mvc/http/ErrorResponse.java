package org.jfantasy.framework.spring.mvc.http;

import org.jfantasy.framework.util.common.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class ErrorResponse {

    private Date timestamp;
    private float code;
    private String path;
    private String message;
    private String exception;
    private List<Error> errors;

    ErrorResponse(HttpServletRequest request){
        this.timestamp = DateUtil.now();
        this.path = request.getRequestURI();
    }

    public String getMessage() {
        return message;
    }

    void addError(String name, String message) {
        if (this.errors == null) {
            this.errors = new ArrayList<>();
        }
        this.errors.add(new Error(name, message));
    }

    public float getCode() {
        return code;
    }

    public List<Error> getErrors() {
        return errors;
    }

    public void setCode(float code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public void setErrors(List<Error> errors) {
        this.errors = errors;
    }
}