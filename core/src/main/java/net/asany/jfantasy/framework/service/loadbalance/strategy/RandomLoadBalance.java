package net.asany.jfantasy.framework.service.loadbalance.strategy;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import net.asany.jfantasy.framework.service.loadbalance.LoadBalance;

/**
 * 最小连接数策略
 *
 * @author limaofeng
 */
public class RandomLoadBalance implements LoadBalance {

  public RandomLoadBalance() {}

  @Override
  public String select(List<String> servers) {
    int randomNum = ThreadLocalRandom.current().nextInt(servers.size());
    return servers.get(randomNum);
  }
}
