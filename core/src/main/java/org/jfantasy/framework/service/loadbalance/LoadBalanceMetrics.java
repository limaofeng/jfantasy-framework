package org.jfantasy.framework.service.loadbalance;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.slf4j.Slf4j;

/**
 * 负载均衡指标
 *
 * @author limaofeng
 */
@Slf4j
public class LoadBalanceMetrics {

  private final ConcurrentHashMap<String, AtomicLong> serverConnectionsMap =
      new ConcurrentHashMap<>();
  private final AtomicLong totalRequests = new AtomicLong();

  public Map<String, Long> getServerConnections() {
    Map<String, Long> connectionsMap = new HashMap<>(10);
    for (Map.Entry<String, AtomicLong> entry : serverConnectionsMap.entrySet()) {
      String server = entry.getKey();
      Long connections = entry.getValue().longValue();
      connectionsMap.put(server, connections);
    }
    return connectionsMap;
  }

  public long getTotalRequests() {
    return totalRequests.get();
  }

  private AtomicLong addServer(String key) {
    return serverConnectionsMap.putIfAbsent(key, new AtomicLong());
  }

  public void removeServer(String key) {
    serverConnectionsMap.remove(key);
  }

  private AtomicLong getServer(String key) {
    return serverConnectionsMap.computeIfAbsent(key, k -> this.addServer(key));
  }

  public void incrementRequest(String key) {
    totalRequests.incrementAndGet();
    if(getServer(key).incrementAndGet() == Integer.MAX_VALUE)  {
        getServer(key).compareAndSet(Integer.MAX_VALUE, 0);
    }
  }

  public void incrementSuccess(String key) {
    getServer(key).decrementAndGet();
  }

  public void incrementError(String key, Throwable e) {
    log.error(e.getMessage(), e);
    getServer(key).decrementAndGet();
  }
}
