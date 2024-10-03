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
