package org.jfantasy.framework.dao.mybatis;

import java.util.Properties;

public interface ConfigurationPropertiesCustomizer {
  void apply(Properties properties);
}
