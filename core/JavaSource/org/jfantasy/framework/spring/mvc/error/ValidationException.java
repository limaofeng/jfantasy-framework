package org.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

public class ValidationException extends RestException{

    private int code = 42;

    public ValidationException(int code, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), message);
        this.code = code;
    }

    public ValidationException(int code, String message, Object state) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), message);
        this.code = code;
        this.setState(state);
    }

    public ValidationException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

}
