package net.asany.jfantasy.framework.dao.hibernate.converter;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import net.asany.jfantasy.framework.error.User;
import org.junit.jupiter.api.Test;

class ListConverterTest {

  @Test
  void convertToEntityAttribute() {
    ListConverter<User> converter = new UserListConverter();

    List<User> users = converter.convertToEntityAttribute("[{\"name\": \"test\"}]");

    assert !users.isEmpty();
  }

  static class UserListConverter extends ListConverter<User> {}
}
