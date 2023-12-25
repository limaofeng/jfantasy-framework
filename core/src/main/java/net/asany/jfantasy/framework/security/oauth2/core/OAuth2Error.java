package net.asany.jfantasy.framework.security.oauth2.core;

import org.springframework.util.Assert;

public class OAuth2Error {
  private final String errorCode;
  private final String description;
  private final String uri;

  public OAuth2Error(String errorCode) {
    this(errorCode, null, null);
  }

  public OAuth2Error(String errorCode, String description, String uri) {
    Assert.hasText(errorCode, "errorCode cannot be empty");
    this.errorCode = errorCode;
    this.description = description;
    this.uri = uri;
  }

  public final String getErrorCode() {
    return this.errorCode;
  }

  public final String getDescription() {
    return this.description;
  }

  public final String getUri() {
    return this.uri;
  }

  @Override
  public String toString() {
    return "["
        + this.getErrorCode()
        + "] "
        + ((this.getDescription() != null) ? this.getDescription() : "");
  }
}
