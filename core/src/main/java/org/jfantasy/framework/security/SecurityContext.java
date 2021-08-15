package org.jfantasy.framework.security;

import lombok.Data;
import org.jfantasy.framework.security.authentication.Authentication;

@Data
public class SecurityContext {

  private Authentication authentication;
}
