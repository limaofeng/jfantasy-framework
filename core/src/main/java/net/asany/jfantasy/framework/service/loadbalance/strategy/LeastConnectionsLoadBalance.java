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
package net.asany.jfantasy.framework.service.loadbalance.strategy;

import java.util.List;
import java.util.Map;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalance;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalanceMetrics;

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
