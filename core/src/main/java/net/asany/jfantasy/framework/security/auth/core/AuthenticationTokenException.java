package net.asany.jfantasy.framework.security.auth.core;

import lombok.Getter;
import net.asany.jfantasy.framework.security.AuthenticationException;
import org.springframework.util.Assert;

@Getter
public class AuthenticationTokenException extends AuthenticationException {

  private final AuthError error;

  public AuthenticationTokenException(String errorCode) {
    this(new AuthError(errorCode));
  }

  public AuthenticationTokenException(AuthError error) {
    this(error, error.getDescription());
  }

  public AuthenticationTokenException(AuthError error, Throwable cause) {
    this(error, cause.getMessage(), cause);
  }

  public AuthenticationTokenException(AuthError error, String message) {
    this(error, message, null);
  }

  public AuthenticationTokenException(AuthError error, String message, Throwable cause) {
    super(message, cause);
    Assert.notNull(error, "error cannot be null");
    this.error = error;
  }
}
