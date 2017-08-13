package org.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

public class RestException extends RuntimeException {

    private int statusCode = HttpStatus.BAD_REQUEST.value();
    private Object state;

    public RestException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public RestException(String message) {
        super(message);
    }

    public int getStatusCode() {
        return statusCode;
    }

    public <T> T getState() {
        return (T)state;
    }

    public void setState(Object state) {
        this.state = state;
    }
}
