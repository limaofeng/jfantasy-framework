package net.asany.jfantasy.framework.service.loadbalance;

import java.util.List;
import net.asany.jfantasy.framework.service.loadbalance.strategy.LeastConnectionsLoadBalance;
import net.asany.jfantasy.framework.service.loadbalance.strategy.RandomLoadBalance;
import net.asany.jfantasy.framework.service.loadbalance.strategy.RoundRobinLoadBalance;

/**
 * 负载均衡器
 *
 * @author limaofeng
 */
public interface LoadBalance {

  /**
   * 随机负载均衡
   *
   * @return 返回负载均衡器
   */
  static LoadBalance random() {
    return new RandomLoadBalance();
  }

  /**
   * 轮询负载均衡
   *
   * @param metrics 指标
   * @return 返回负载均衡器
   */
  static LoadBalance roundRobin(LoadBalanceMetrics metrics) {
    return new RoundRobinLoadBalance(metrics);
  }

  /**
   * 最小连接数负载均衡
   *
   * @param metrics 指标
   * @return 返回负载均衡器
   */
  static LoadBalance leastConnection(LoadBalanceMetrics metrics) {
    return new LeastConnectionsLoadBalance(metrics);
  }

  /**
   * 选择一个地址
   *
   * @param servers 可用的服务器
   * @return 返回地址
   */
  String select(List<String> servers);
}
