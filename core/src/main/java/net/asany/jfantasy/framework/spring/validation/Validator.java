package net.asany.jfantasy.framework.spring.validation;

/**
 * 验证接口
 *
 * @author limaofeng
 * @param <T>
 */
public interface Validator<T> {

  /**
   * 验证方法
   *
   * @param value 需要验证的值
   * @throws ValidationException 验证错误抛出的异常
   */
  void validate(T value) throws ValidationException;
}
