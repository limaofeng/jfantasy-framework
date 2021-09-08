package org.jfantasy.framework.spring.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * 自定义验证
 *
 * @author limaofeng
 */
public class UseConstraintValidator implements ConstraintValidator<Use, Object> {

  private static final Log LOG = LogFactory.getLog(UseConstraintValidator.class);

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
      LOG.error(e.getMessage(), e);
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
