package net.asany.jfantasy.framework.security.auth.core;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.framework.security.auth.TokenType;

@Data
@Builder
public class DefaultAuthenticationDetails implements AuthenticationDetails {

  /**
   * 认证类型
   *
   * <p>1. JWT
   */
  private TokenType tokenType;

  /**
   * 客户端的ID<br>
   * 分配的 apiKey
   */
  @JsonProperty("client_id")
  private String clientId;

  @JsonProperty("expires_at")
  private Instant expiresAt;
}
