package net.asany.jfantasy.graphql.client;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.util.HashMap;

class GraphQLWebSocketClientTest {

  @BeforeEach
  void setUp() {
  }

  @Test
  @SneakyThrows
  public void testSubscription() {
    GraphQLWebSocketClient client = new GraphQLWebSocketClient("ws://localhost:8080/subscriptions");
    client.connect();

    client.subscribe(QueryPayload.builder().query("""
      subscription userChange {
        userChange {
          id
          username
        }
      }
      """).variables(new HashMap<>()).operationName("userChange").build(), message -> {
      System.out.println("Received: " + message);
    });

    // 等待1分钟以确保WebSocket可以接收消息
    Thread.sleep(60000);
  }
}