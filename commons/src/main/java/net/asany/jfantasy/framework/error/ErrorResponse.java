package net.asany.jfantasy.framework.error;

import java.io.Serializable;
import java.util.*;
import lombok.Data;
import net.asany.jfantasy.framework.util.common.DateUtil;

/**
 * @author limaofeng
 */
@Data
public class ErrorResponse implements Serializable {
  /** HTTP 状态码 */
  private int status;

  /** 错误发生时间 */
  private Date timestamp;

  /** 状态码错误说明 */
  private String error;

  /** 定义的具体错误码 */
  private String code;

  /** 错误消息 */
  private String message;

  /** 原始异常信息 */
  private String exception;

  /** 当验证错误时，各项具体的错误信息 */
  private List<FieldValidationError> fieldErrors = new ArrayList<>();

  /** 定义的返回数据 */
  private Map<String, Object> data = new HashMap<>();

  public ErrorResponse() {
    this.timestamp = DateUtil.now();
  }

  public void addFieldError(FieldValidationError error) {
    this.fieldErrors.add(error);
  }

  public void addDataValue(String key, Object value) {
    this.data.put(key, value);
  }

  public void addData(Map<String, Object> data) {
    this.data.putAll(data);
  }
}
