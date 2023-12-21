package org.jfantasy.graphql.gateway.error;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jfantasy.framework.jackson.JSON;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataFetchGraphQLErrorTest {

  @BeforeEach
  void setUp() {
    JSON.initialize();
  }

  @AfterEach
  void tearDown() {
  }

  @Test
  void json() {
    String jsonStr = """
      [
          {
            "message": "Sort must not be null",
            "locations": [
              {
                "line": 2,
                "column": 3
              }
            ],
            "path": [
              "landingPagesConnection"
            ],
            "extensions": {
              "classification": "DataFetchingException",
              "code": "000000",
              "timestamp": "2023-12-21 13:53:57"
            }
          },
          {
            "message": "Sort must not be null",
            "locations": [
              {
                "line": 11,
                "column": 3
              }
            ],
            "path": [
              "a22"
            ],
            "extensions": {
              "classification": "DataFetchingException",
              "code": "000000",
              "timestamp": "2023-12-21 13:53:57"
            }
          }
        ]  
    """;
    List<DataFetchGraphQLError> errors = JSON.deserialize(jsonStr, new TypeReference<>() {
    });

    assertEquals(2, errors.size());
  }

}