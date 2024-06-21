package net.asany.jfantasy.graphql.client.error;

import static org.junit.jupiter.api.Assertions.*;

import net.asany.jfantasy.framework.jackson.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DataFetchGraphQLErrorTest {

  @BeforeEach
  void setUp() {}

  @AfterEach
  void tearDown() {}

  @Test
  public void jsonToError() {
    String body =
        "{\"locations\":[{\"line\":1,\"column\":16}],\"path\":[\"viewer\"],\"extensions\":{\"classification\":\"AuthenticatedError\",\"code\":\"100401\",\"timestamp\":\"2024-06-20 15:10:42\"}}";
    DataFetchGraphQLError error = JSON.deserialize(body, DataFetchGraphQLError.class);
    assertEquals("100401", error.getExtensions().get("code"));
    assertEquals("2024-06-20 15:10:42", error.getExtensions().get("timestamp"));
  }
}
