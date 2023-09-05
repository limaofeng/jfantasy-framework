package org.jfantasy.framework.security.authentication;

/**
 * AuthenticationDetailsSource
 *
 * @author limaofeng
 */
public interface AuthenticationDetailsSource<C, T> {

  /**
   * buildDetails
   *
   * @param context C
   * @return T
   */
  T buildDetails(C context);
}
