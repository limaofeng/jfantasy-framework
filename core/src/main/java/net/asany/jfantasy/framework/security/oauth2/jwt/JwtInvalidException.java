package net.asany.jfantasy.framework.security.oauth2.jwt;

import com.nimbusds.jose.JOSEException;

public class JwtInvalidException extends JOSEException {
  public JwtInvalidException(String message) {
    super(message);
  }
}
