package org.jfantasy.framework.dao.mybatis;

import java.util.Properties;

/**
 * 自定义配置
 *
 * @author limaofeng
 */
public interface ConfigurationPropertiesCustomizer {
  /**
   * 自定义配置
   *
   * @param properties 配置
   */
  void customize(Properties properties);
}
