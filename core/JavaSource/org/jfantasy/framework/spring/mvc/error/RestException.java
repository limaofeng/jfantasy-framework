package org.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

import java.io.Serializable;

public class RestException extends RuntimeException {

    private int statusCode = HttpStatus.BAD_REQUEST.value();
    private Serializable state;

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

    public Serializable getState() {
        return state;
    }

    public void setState(Serializable state) {
        this.state = state;
    }
}
