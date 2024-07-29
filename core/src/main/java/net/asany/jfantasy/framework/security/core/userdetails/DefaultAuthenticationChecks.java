package net.asany.jfantasy.framework.security.core.userdetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;

/**
 * @author limaofeng
 */
public class DefaultAuthenticationChecks implements UserDetailsChecker {

  private final List<UserDetailsChecker> checkers = new ArrayList<>();

  public DefaultAuthenticationChecks() {}

  public DefaultAuthenticationChecks(UserDetailsChecker checker) {
    this.checkers.add(checker);
  }

  @Override
  public void check(UserDetails user, AuthenticationToken authenticationToken) {
    for (UserDetailsChecker checker : checkers) {
      if (!checker.needsCheck(authenticationToken)) {
        continue;
      }
      checker.check(user, authenticationToken);
    }
  }

  public void addCheckers(UserDetailsChecker... checkers) {
    this.checkers.addAll(Arrays.asList(checkers));
  }
}
