package net.asany.jfantasy.framework.security.authentication;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

/** ApiKey Principal */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiKeyPrincipal implements AuthenticatedPrincipal {
  private String name;

  @Override
  public Long getId() {
    return 0L;
  }
}
