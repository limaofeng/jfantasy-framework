package org.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

public class ValidationException extends RestException{

    private int code;

    public ValidationException(int code, String message) {
        super(HttpStatus.UNPROCESSABLE_ENTITY.value(), message);
        this.code = code;
    }

    public ValidationException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

}
