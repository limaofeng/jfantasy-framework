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
