package org.jfantasy.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "spring.sequence")
public class SequenceProperties {

  private int poolSize = 100;
}
