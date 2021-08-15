package org.jfantasy.framework.security.authentication;

public interface AuthenticationDetailsSource<C, T> {
  T buildDetails(C context);
}
