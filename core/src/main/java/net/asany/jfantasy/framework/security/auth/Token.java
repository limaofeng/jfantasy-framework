package net.asany.jfantasy.framework.security.auth;

public record Token(AuthType type, AuthTokenType tokenType, String value) {
  @Override
  public String toString() {
    return type + ":" + tokenType + ":" + value;
  }
}
