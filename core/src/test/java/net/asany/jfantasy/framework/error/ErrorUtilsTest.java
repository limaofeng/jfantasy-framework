package net.asany.jfantasy.framework.error;

import net.asany.jfantasy.framework.spring.util.ValidationUtil;
import org.junit.jupiter.api.Test;

class ErrorUtilsTest {

  @Test
  void errorCode() {
    System.out.println(ErrorUtils.errorCode(new ValidationException("xxxx")));
  }

  @Test
  void validate() {
    User user = new User();
    ValidationUtil.validate(user);
  }
}
