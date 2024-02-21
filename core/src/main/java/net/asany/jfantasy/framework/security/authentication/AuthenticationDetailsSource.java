package net.asany.jfantasy.framework.security.authentication;

import net.asany.jfantasy.framework.security.auth.core.AuthenticationDetails;

/**
 * AuthenticationDetailsSource
 *
 * @author limaofeng
 */
public interface AuthenticationDetailsSource<C, T extends AuthenticationDetails> {

  /**
   * buildDetails
   *
   * @param context C
   * @return T
   */
  T buildDetails(C context);
}
