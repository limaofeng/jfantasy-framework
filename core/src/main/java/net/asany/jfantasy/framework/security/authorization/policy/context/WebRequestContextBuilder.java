package net.asany.jfantasy.framework.security.authorization.policy.context;

import jakarta.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.web.authentication.WebAuthenticationDetails;

public class WebRequestContextBuilder implements RequestContextBuilder {
  @Override
  public boolean supports(Authentication authentication) {
    return authentication.getDetails() instanceof WebAuthenticationDetails;
  }

  @Override
  public RequestContext build(Authentication authentication) {
    WebAuthenticationDetails webDetails = authentication.getDetails();

    HttpServletRequest request = webDetails.getRequest();

    String username = null;
    Collection<String> roles = new ArrayList<>();

    if (authentication.isAuthenticated()) {
      username = authentication.getName();
      roles =
          authentication.getAuthorities().stream()
              .map(GrantedAuthority::getAuthority)
              .filter(role -> role.startsWith("ROLE_"))
              .collect(Collectors.toList());
    }

    String sourceIp = request.getRemoteAddr();
    boolean isSecureTransport = request.isSecure();

    return RequestContext.builder()
        .userId(username)
        .roles(roles)
        .sourceIp(sourceIp)
        .secureTransport(isSecureTransport)
        .build();
  }
}
