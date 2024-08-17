package net.asany.jfantasy.framework.security.auth.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.asany.jfantasy.framework.security.auth.AuthType;
import net.asany.jfantasy.framework.security.auth.TokenUsage;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractAuthenticationDetails implements AuthenticationDetails {
  protected AuthType authType;
  protected TokenUsage tokenUsage;

  @JsonProperty("client_id")
  protected String clientId;

  @JsonIgnore protected ClientDetails clientDetails;

  @JsonProperty("client_secret")
  protected ClientSecret clientSecret;

  @JsonProperty("expires_at")
  protected Instant expiresAt;
}
