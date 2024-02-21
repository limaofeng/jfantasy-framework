package net.asany.jfantasy.framework.security.auth.oauth2.server;

import org.springframework.http.HttpStatus;

public class BearerTokenErrors {
  private static final BearerTokenError DEFAULT_INVALID_REQUEST = invalidRequest("Invalid request");

  private static final BearerTokenError DEFAULT_INVALID_TOKEN = invalidToken("Invalid token");

  private static final BearerTokenError DEFAULT_INSUFFICIENT_SCOPE =
      insufficientScope("Insufficient scope", null);

  private static final String DEFAULT_URI = "https://tools.ietf.org/html/rfc6750#section-3.1";

  private BearerTokenErrors() {}

  public static BearerTokenError invalidRequest(String message) {
    try {
      return new BearerTokenError(
          BearerTokenErrorCodes.INVALID_REQUEST, HttpStatus.BAD_REQUEST, message, DEFAULT_URI);
    } catch (IllegalArgumentException ex) {
      return DEFAULT_INVALID_REQUEST;
    }
  }

  public static BearerTokenError invalidToken(String message) {
    try {
      return new BearerTokenError(
          BearerTokenErrorCodes.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, message, DEFAULT_URI);
    } catch (IllegalArgumentException ex) {
      return DEFAULT_INVALID_TOKEN;
    }
  }

  public static BearerTokenError insufficientScope(String message, String scope) {
    try {
      return new BearerTokenError(
          BearerTokenErrorCodes.INSUFFICIENT_SCOPE,
          HttpStatus.FORBIDDEN,
          message,
          DEFAULT_URI,
          scope);
    } catch (IllegalArgumentException ex) {
      return DEFAULT_INSUFFICIENT_SCOPE;
    }
  }
}
