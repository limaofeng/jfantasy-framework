package net.asany.jfantasy.framework.error;

import net.asany.jfantasy.framework.jackson.models.User;
import org.junit.jupiter.api.Test;

class ErrorUtilsTest {

  @Test
  void errorCode() {
    System.out.println(ErrorUtils.errorCode(new ValidationException("xxxx")));
  }

  @Test
  void validate() {
    User user = new User();
    ErrorUtils.validate(user);
  }
}
