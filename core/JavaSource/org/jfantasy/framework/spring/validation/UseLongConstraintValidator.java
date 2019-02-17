package org.jfantasy.framework.spring.validation;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ClassUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UseLongConstraintValidator implements ConstraintValidator<UseLong, Long> {

    private static final Log LOG = LogFactory.getLog(UseLongConstraintValidator.class);

    private Validator validator;

    @Override
    public void initialize(UseLong use) {
        Class<? extends Validator> clazz = use.vali();
        validator = SpringContextUtil.getBeanByType(clazz);
        if (validator == null) {
            validator = ClassUtil.newInstance(clazz);
        }
    }

    @Override
    public boolean isValid(Long str, ConstraintValidatorContext constraintValidatorContext) {
        try {
            validator.validate(str);
        } catch (ValidationException e) {
            LOG.error(e.getMessage(),e);
            constraintValidatorContext.disableDefaultConstraintViolation();
            constraintValidatorContext.buildConstraintViolationWithTemplate(e.getMessage()).addConstraintViolation();
            return false;
        }
        return true;
    }

}
