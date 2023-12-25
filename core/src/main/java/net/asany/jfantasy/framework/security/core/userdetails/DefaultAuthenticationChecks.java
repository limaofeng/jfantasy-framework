package net.asany.jfantasy.framework.security.core.userdetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
  public void check(UserDetails user) {
    for (UserDetailsChecker checker : checkers) {
      checker.check(user);
    }
  }

  public void addCheckers(UserDetailsChecker... checkers) {
    this.checkers.addAll(Arrays.asList(checkers));
  }
}
