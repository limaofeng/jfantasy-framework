package net.asany.jfantasy.graphql.client;

import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.framing.Framedata;
import org.java_websocket.framing.PingFrame;
import org.java_websocket.framing.PongFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
public class GraphQLWebSocketClient {

  private final String uri;
  private final Map<String, String> headers;
  private final Map<String, Consumer<Object>> listeners = new HashMap<>();
  private final AtomicLong maxId = new AtomicLong(1);
  private final Map<String, Object> connectionParams = new HashMap<>();
  @Getter
  private long latency = 0;
  private final ScheduledExecutorService executorService;

  private WebSocketClient webSocketClient;

  private ScheduledFuture<?> pingTask;

  public GraphQLWebSocketClient(String serverUri) throws URISyntaxException {
    this(serverUri, new HashMap<>());
  }

  public GraphQLWebSocketClient(String uri, Map<String, String> headers) throws URISyntaxException {
    this.uri = uri;
    this.headers = headers;

    this.webSocketClient = this.createWebSocketClient();
    executorService = Executors.newScheduledThreadPool(5);
  }

  private WebSocketClient createWebSocketClient() throws URISyntaxException {
    Map<String, String> newHeaders = new HashMap<>(headers);
    newHeaders.put("Sec-WebSocket-Protocol", "graphql-ws");
    newHeaders.put("Sec-WebSocket-Key", StringUtil.generateNonceString(22) + "==");
    newHeaders.put("Sec-WebSocket-Version", "13");
    return new WebSocketClient(new URI(uri), new Draft_6455(), newHeaders, 0) {
      @Override
      public void onOpen(ServerHandshake serverHandshake) {
        GraphQLWebSocketClient.this.onOpen(serverHandshake);
      }

      @Override
      public void onMessage(String s) {
        GraphQLWebSocketClient.this.onMessage(s);
      }

      @Override
      public void onClose(int i, String s, boolean b) {
        GraphQLWebSocketClient.this.onClose(i, s, b);
      }

      @Override
      public void onError(Exception e) {
        GraphQLWebSocketClient.this.onError(e);
      }

      @Override
      public PingFrame onPreparePing(WebSocket conn) {
        return GraphQLWebSocketClient.this.onPreparePing(conn);
      }

      @Override
      public void onWebsocketPong(WebSocket conn, Framedata f) {
        GraphQLWebSocketClient.this.onWebsocketPong(conn, f);
      }
    };
  }

  private void onOpen(ServerHandshake serverHandshake) {
    this.webSocketClient.send("{\"type\":\"connection_init\",\"payload\":%s}".formatted(JSON.serialize(connectionParams)));
    pingTask = executorService.scheduleAtFixedRate(() -> this.webSocketClient.sendPing(), 10, 5, TimeUnit.SECONDS);
  }

  public Runnable subscribe(QueryPayload payload, Consumer<Object> handler) {
    String subscriptionId = String.valueOf(maxId.get());
    String graphqlQuery = """
      {"id": "%s","type":"start","payload":%s}
      """.formatted(subscriptionId, JSON.serialize(payload));
    this.webSocketClient.send(graphqlQuery);
    listeners.put(subscriptionId, handler);
    maxId.incrementAndGet();
    return () -> unsubscribe(subscriptionId);
  }

  private void unsubscribe(String subscriptionId) {
    String graphqlQuery = """
      {"id": "%s","type":"stop"}
      """.formatted(subscriptionId);
    this.webSocketClient.send(graphqlQuery);
    listeners.remove(subscriptionId);
  }

  private void onMessage(String message) {
    OperationMessage operationMessage = JSON.deserialize(message, OperationMessage.class);
    if (Objects.requireNonNull(operationMessage.getType()) == OperationMessage.Type.GQL_DATA) {
      this.listeners.get(operationMessage.getId()).accept(operationMessage.getPayload());
    } else {
      System.out.println("Received: " + message);
    }
  }

  private void onClose(int code, String reason, boolean remote) {
    log.info("Closed with exit code {} additional info: {}", code, reason);
    if(pingTask != null && !pingTask.isCancelled()) {
      pingTask.cancel(true);
    }
  }

  private PingFrame onPreparePing(WebSocket conn) {
    PingFrame pingFrame = this.webSocketClient.onPreparePing(conn);
    pingFrame.setPayload(ByteBuffer.wrap(longToBytes(System.currentTimeMillis())));
    return pingFrame;
  }

  private void onWebsocketPong(WebSocket conn, Framedata f) {
    if (f instanceof PongFrame) {
      ByteBuffer payload = f.getPayloadData();
      long sendTime = bytesToLong(payload.array());
      this.latency = System.currentTimeMillis() - sendTime;
      System.out.println("Latency for pong: " + latency + " ms");
    }
  }

  private void onError(Exception ex) {
    log.error("An error occurred: {}", ex);
  }

  @SneakyThrows
  public GraphQLWebSocketClient withBearerAuth(String token) {
    Map<String, String> headers = new HashMap<>(this.headers);
    GraphQLWebSocketClient newClient = new GraphQLWebSocketClient(this.uri, headers);
    newClient.connectionParams.put("Authorization", "Bearer " + token);
    return newClient;
  }

  public int activeSubscriptions() {
    return listeners.size();
  }

  private static byte[] longToBytes(long value) {
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);  // Long.BYTES 为 8，因为long是8字节
    buffer.putLong(value);
    return buffer.array();
  }

  private static long bytesToLong(byte[] bytes) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);
    return buffer.getLong();
  }

  public void connectBlocking(long timeout, TimeUnit timeUnit) throws InterruptedException {
    this.webSocketClient.connectBlocking(timeout, timeUnit);
    log.info("connect to {}", this.webSocketClient.getURI());
  }

  public void connect() {
    this.webSocketClient.connect();
    log.info("connect to {}", this.webSocketClient.getURI());
  }

  public void close() {
    this.webSocketClient.close();
  }
}
