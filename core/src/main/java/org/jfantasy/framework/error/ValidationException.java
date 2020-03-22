package org.jfantasy.framework.error;

/**
 * @author limaofeng
 */
public class ValidationException extends RuntimeException {

    private String code = "420000";

    public ValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ValidationException(String message) {
        super(message);
    }

    public String getCode() {
        return code;
    }

}
