package net.asany.jfantasy.framework.spring.mvc.error;

import net.asany.jfantasy.framework.error.ValidationException;
import org.junit.jupiter.api.Test;

public class ValidationExceptionTest {

  @Test
  public void getCode() throws Exception {
    ValidationException exception = new ValidationException("501010", "");
    System.out.println(exception.getCode());
  }
}
