package net.asany.jfantasy.autoconfigure.properties;

import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.security")
public class SecurityProperties {
  private Map<String, String> tokenServices;

  private final AccessTokenProperties accessToken = new AccessTokenProperties();

  @Data
  public static class AccessTokenProperties {
    private boolean validate = true;
    private boolean refresh = true;
  }
}
