package net.asany.jfantasy.framework.security.authorization.policy.context;

import java.util.Collection;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder", toBuilder = true)
public class RequestContext {
  private String userId;
  private String username;
  private Collection<String> roles;
  private String sourceIp;
  private boolean secureTransport;
}
