/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.security.core.userdetails;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;

/**
 * @author limaofeng
 */
@NoArgsConstructor
public class DefaultAuthenticationChecks implements UserDetailsChecker {

  private final List<UserDetailsChecker> checkers = new ArrayList<>();

  public DefaultAuthenticationChecks(UserDetailsChecker checker) {
    this.checkers.add(checker);
  }

  @Override
  public void check(UserDetails user, AuthenticationToken<?> authenticationToken) {
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
