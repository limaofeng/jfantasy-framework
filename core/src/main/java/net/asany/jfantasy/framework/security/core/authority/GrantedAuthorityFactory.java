package net.asany.jfantasy.framework.security.core.authority;

import java.util.HashMap;
import java.util.Map;
import lombok.Setter;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class GrantedAuthorityFactory {
  private final Map<String, AuthorityBuilder> builderMap = new HashMap<>();
  @Setter private AuthorityBuilder defaultAuthorityBuilder;

  public void registerBuilder(String prefix, AuthorityBuilder builder) {
    builderMap.put(prefix, builder);
  }

  public GrantedAuthority createAuthority(String authorityString) {
    for (Map.Entry<String, AuthorityBuilder> entry : builderMap.entrySet()) {
      if (authorityString.startsWith(entry.getKey())) {
        return entry.getValue().buildAuthority(authorityString);
      }
    }
    if (defaultAuthorityBuilder != null) {
      return defaultAuthorityBuilder.buildAuthority(authorityString);
    }
    throw new IllegalArgumentException("No builder found for authority: " + authorityString);
  }
}
