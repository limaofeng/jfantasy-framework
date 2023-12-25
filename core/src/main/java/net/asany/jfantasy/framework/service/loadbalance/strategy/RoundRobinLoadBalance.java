package net.asany.jfantasy.framework.service.loadbalance.strategy;

import java.util.List;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalance;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;

/**
 * 轮询策略
 *
 * @author limaofeng
 */
public class RoundRobinLoadBalance implements LoadBalance {

  private final LoadBalanceMetrics metrics;

  public RoundRobinLoadBalance(LoadBalanceMetrics metrics) {
    this.metrics = metrics;
  }

  @Override
  public String select(List<String> servers) {
    long totalRequests = metrics.getTotalRequests();
    long index = totalRequests % servers.size();
    return servers.get(Long.valueOf(index).intValue());
  }
}
