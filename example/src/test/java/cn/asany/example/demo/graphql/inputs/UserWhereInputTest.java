package cn.asany.example.demo.graphql.inputs;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

class UserWhereInputTest {

  @Test
  void initPropertyFilter() {
    UserWhereInput whereInput = new UserWhereInput();
    assertNotNull(whereInput.toFilter());
  }
}
