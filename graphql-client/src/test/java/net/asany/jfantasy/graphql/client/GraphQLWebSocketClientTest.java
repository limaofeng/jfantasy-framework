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
package net.asany.jfantasy.graphql.client;

import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GraphQLWebSocketClientTest {

  @BeforeEach
  void setUp() {}

  @Test
  @SneakyThrows
  public void testSubscription() {
    GraphQLWebSocketClient client = new GraphQLWebSocketClient("ws://127.0.0.1:8080/subscriptions");
    client.connectBlocking(1, TimeUnit.MINUTES);

    //    client.subscribe(QueryPayload.builder().query("""
    //      subscription userChange {
    //        userChange {
    //          id
    //          username
    //        }
    //      }
    //      """).variables(new HashMap<>()).operationName("userChange").build(), message -> {
    //      System.out.println("Received: " + message);
    //    });

    // 等待1分钟以确保WebSocket可以接收消息
    Thread.sleep(60000);
  }
}
