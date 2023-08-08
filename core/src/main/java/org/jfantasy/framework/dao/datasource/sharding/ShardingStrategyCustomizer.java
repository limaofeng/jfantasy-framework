package org.jfantasy.framework.dao.datasource.sharding;

import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;

/**
 * Sharding 策略定制器
 *
 * @author limaofeng
 */
public interface ShardingStrategyCustomizer {

  /**
   * 定制
   *
   * @param conf ShardingRuleConfiguration
   */
  void customize(ShardingRuleConfiguration conf);
}
