package org.jfantasy.framework.error;

import java.util.*;
import lombok.Data;
import org.jfantasy.framework.spring.mvc.http.Error;
import org.jfantasy.framework.util.common.DateUtil;

/**
 * @author limaofeng
 */
@Data
public class ErrorResponse {
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
  private List<Error> fields = new ArrayList<>();

  /** 定义的返回数据 */
  private Map<String, Object> data = new HashMap<>();

  public ErrorResponse() {
    this.timestamp = DateUtil.now();
  }

  public void addFieldError(String name, String message) {
    this.fields.add(new Error(name, message));
  }

  public void addData(String key, Object value) {
    this.data.put(key, value);
  }
}
