package org.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

public class SecurityException extends RestException {

    private static final int prefix = HttpStatus.FORBIDDEN.value() * 100;

    private int code;

    public SecurityException(int code, String message) {
        super(HttpStatus.FORBIDDEN.value(), message);
        this.code = prefix + code;
    }

    public SecurityException(String message) {
        super(message);
    }

    public int getCode() {
        return code;
    }

}
