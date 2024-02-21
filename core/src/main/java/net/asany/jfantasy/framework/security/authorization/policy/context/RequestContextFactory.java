package net.asany.jfantasy.framework.security.authorization.policy.context;

import java.util.List;
import net.asany.jfantasy.framework.security.authentication.Authentication;

public class RequestContextFactory {

  private final List<RequestContextBuilder> builders;

  public RequestContextFactory(List<RequestContextBuilder> builders) {
    this.builders = builders;
  }

  public RequestContext create(Authentication authentication) {
    RequestContextBuilder requestContextBuilder =
        this.builders.stream()
            .filter(builder -> builder.supports(authentication))
            .findFirst()
            .orElseThrow(
                () -> new RuntimeException("No RequestContextBuilder found for authentication"));
    return requestContextBuilder.build(authentication);
  }
}
