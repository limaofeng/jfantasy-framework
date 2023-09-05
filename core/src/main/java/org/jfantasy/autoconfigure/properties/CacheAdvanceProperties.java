package org.jfantasy.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 缓存配置
 *
 * @author limaofeng
 */
@Data
@ConfigurationProperties(prefix = "spring.cache.advance")
public class CacheAdvanceProperties {

  private String beanNames;
}
