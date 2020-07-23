package org.jfantasy.framework.error;

import java.util.Map;

/**
 * @author limaofeng
 */
public class ValidationException extends RuntimeException {

    private String code = "420000";

    private transient Map<String, Object> data;

    public ValidationException(String code, String message) {
        super(message);
        this.code = code;
    }

    public ValidationException(String code, String message, Map<String, Object> data) {
        super(message);
        this.code = code;
        this.data = data;
    }


    public ValidationException(String message) {
        super(message);
    }

    public String getCode() {
        return code;
    }

    public Map<String, Object> getData() {
        return data;
    }
}