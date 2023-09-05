package org.jfantasy.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Cuckoo 配置
 *
 * @author limaofeng
 */
@Data
@ConfigurationProperties(prefix = "cuckoo")
public class CuckooProperties {

  private boolean rebuild;
  private boolean enable = true;

  private int batchSize = 100;
}
