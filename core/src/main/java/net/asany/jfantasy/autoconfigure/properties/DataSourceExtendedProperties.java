package net.asany.jfantasy.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 数据源 代理配置
 *
 * @author limaofeng
 */
@Data
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceExtendedProperties {
  private boolean proxy;

  private boolean sharding;
}
