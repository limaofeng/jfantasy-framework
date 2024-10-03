/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
