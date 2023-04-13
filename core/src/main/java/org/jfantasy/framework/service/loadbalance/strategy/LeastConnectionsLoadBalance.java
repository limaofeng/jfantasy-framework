package org.jfantasy.framework.service.loadbalance.strategy;

import java.util.List;
import java.util.Map;
import org.jfantasy.framework.service.loadbalance.LoadBalance;
import org.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;

/**
 * 最小连接数策略
 *
 * @author limaofeng
 */
public class LeastConnectionsLoadBalance implements LoadBalance {

  private final LoadBalanceMetrics metrics;

  public LeastConnectionsLoadBalance(LoadBalanceMetrics metrics) {
    this.metrics = metrics;
  }

  @Override
  public String select(List<String> servers) {
    long min = Integer.MAX_VALUE;
    String selectServer = null;
    Map<String, Long> serverConnections = metrics.getServerConnections();
    for (String server : servers) {
      long count = serverConnections.get(server);
      if (count < min) {
        min = count;
        selectServer = server;
      }
    }
    return selectServer;
  }
}
