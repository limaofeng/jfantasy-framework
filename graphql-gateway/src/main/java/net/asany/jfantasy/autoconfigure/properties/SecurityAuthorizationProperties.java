package net.asany.jfantasy.autoconfigure.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@Data
@ConfigurationProperties(prefix = "graphql.gateway.security.authorization")
public class SecurityAuthorizationProperties {
  private boolean enabled = true;
  private Resource configLocation;
}
