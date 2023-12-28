package net.asany.jfantasy.framework.spring.util;

import net.asany.jfantasy.framework.error.ValidationException;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;

public class ValidationUtil {

  public static void validate(Object object) throws ValidationException {
    DataBinder binder = createBinder(object);
    binder.validate(object);
    if (binder.getBindingResult().hasErrors()) {
      throw new ValidationException(binder.getBindingResult());
    }
  }

  private static DataBinder createBinder(Object target) {
    SmartValidator validator = SpringBeanUtils.getBeanByType(SmartValidator.class);
    DataBinder binder = new DataBinder(target);
    binder.setValidator(validator);
    return binder;
  }
}
