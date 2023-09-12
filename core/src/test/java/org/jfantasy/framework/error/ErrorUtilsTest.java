package org.jfantasy.framework.error;

import static org.junit.jupiter.api.Assertions.*;

import org.jfantasy.framework.jackson.models.User;
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
