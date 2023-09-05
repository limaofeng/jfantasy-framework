package org.jfantasy.framework.service.loadbalance;

/**
 * 负载均衡策略
 *
 * @author limaofeng
 */
public enum LoadBalancingStrategy {
  /** 随机 */
  RANDOM,
  /** 轮询 */
  ROUND_ROBIN,
  /** 最小连接数 */
  LEAST_CONNECTIONS,
  /** 最小响应时间 */
  SHORTEST_RESPONSE_TIME
}
