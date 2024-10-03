/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.spring.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;

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
