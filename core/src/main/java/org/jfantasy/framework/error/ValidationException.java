package org.jfantasy.framework.error;

import java.util.Map;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * 验证异常
 *
 * @author limaofeng
 */
@Getter
public class ValidationException extends RuntimeException {

  private String code;
  private BindingResult bindingResult;
  private transient Map<String, Object> data;

  public ValidationException(String code, String message) {
    super(message);
    this.code = code;
  }

  public ValidationException(BindingResult bindingResult) {
    super("输入的数据不合法,详情见 fields 字段");
    this.bindingResult = bindingResult;
  }

  public ValidationException(String message, Map<String, Object> data) {
    super(message);
    this.data = data;
  }

  public ValidationException(String code, String message, Map<String, Object> data) {
    super(message);
    this.code = code;
    this.data = data;
  }

  public ValidationException(String message) {
    super(message);
  }

  public boolean hasFieldErrors() {
    return this.bindingResult != null;
  }
}
