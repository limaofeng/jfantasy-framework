package net.asany.jfantasy.framework.security.auth.core;

import java.util.Set;
import lombok.Builder;
import lombok.Data;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

@Data
@Builder
public class TokenObject {

  private AuthToken accessToken;
  private AuthenticatedPrincipal principal;
  private Set<String> authorities;
}
