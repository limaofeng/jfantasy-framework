package org.jfantasy.framework.spring.mvc.http;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.jfantasy.framework.util.common.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@JsonIgnoreProperties("state")
class ErrorResponse {
    /**
     * 错误发生时间
     */
    private Date timestamp;
    /**
     * 对应浏览器状态
     */
    private int status;
    /**
     * 状态码错误说明
     */
    private String error;
    /**
     * 定义的具体错误码
     */
    private float code;
    /**
     * 请求出错的地址
     */
    private String path;
    /**
     * 错误消息
     */
    private String message;
    /**
     * 原始异常信息
     */
    private String exception;
    /**
     * 当验证错误时，各项具体的错误信息
     */
    private List<Error> errors;
    /**
     * 出错时传入的相关数据,方便后期处理。
     */
    private Object state;

    ErrorResponse(HttpServletRequest request) {
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}