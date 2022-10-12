package org.jfantasy.framework.spring.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 自定义验证
 *
 * @author limaofeng
 */
@Slf4j
public class UseConstraintValidator implements ConstraintValidator<Use, Object> {

  private Validator<Object> validator;
  private String message;

  @Override
  public void initialize(Use use) {
    Class<Validator<Object>> clazz = (Class<Validator<Object>>) use.value();
    message = use.message();
    if (SpringBeanUtils.containsBean(clazz)) {
      validator = SpringBeanUtils.getBeanByType(clazz);
    } else {
      validator = ClassUtil.newInstance(clazz);
    }
  }

  @Override
  public boolean isValid(Object str, ConstraintValidatorContext constraintValidatorContext) {
    try {
      validator.validate(str);
    } catch (ValidationException e) {
      log.error(e.getMessage(), e);
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext
          .buildConstraintViolationWithTemplate(
              StringUtil.isNotBlank(message) ? message : e.getMessage())
          .addConstraintViolation();
      return false;
    }
    return true;
  }
}
