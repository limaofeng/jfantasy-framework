package net.asany.jfantasy.framework.security.core;

public class SimpleAuthenticatedPrincipal implements AuthenticatedPrincipal {

  private final String name;

  public SimpleAuthenticatedPrincipal(String name) {
    this.name = name;
  }

  @Override
  public String getName() {
    return this.name;
  }
}
