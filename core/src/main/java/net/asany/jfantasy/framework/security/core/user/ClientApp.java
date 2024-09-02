package net.asany.jfantasy.framework.security.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.auth.core.ClientDetails;
import net.asany.jfantasy.framework.security.core.AbstractAuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ClientApp extends AbstractAuthenticatedPrincipal implements AuthenticatedPrincipal {

  private Long id;
  private String name;
  @JsonIgnore private ClientDetails clientDetails;

  public ClientApp(ClientDetails clientDetails) {
    this.id = clientDetails.getId();
    this.name = clientDetails.getName();
    this.clientDetails = clientDetails;
  }
}
