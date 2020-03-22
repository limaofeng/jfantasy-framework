package org.jfantasy.framework.error;

import lombok.Data;
import org.jfantasy.framework.spring.mvc.http.Error;
import org.jfantasy.framework.util.common.DateUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author limaofeng
 */
@Data
public class ErrorResponse {
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
    private String code;
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
    private List<Error> fields;

    public ErrorResponse() {
        this.timestamp = DateUtil.now();
    }

    public void addFieldError(String name, String message) {
        if (this.fields == null) {
            this.fields = new ArrayList<>();
        }
        this.fields.add(new Error(name, message));
    }

}