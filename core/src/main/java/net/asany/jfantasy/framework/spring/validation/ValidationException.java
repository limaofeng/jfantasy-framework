package net.asany.jfantasy.framework.spring.validation;

/**
 * 验证异常
 *
 * @author limaofeng
 */
public class ValidationException extends Exception {

  public ValidationException(String message) {
    super(message);
  }
}
