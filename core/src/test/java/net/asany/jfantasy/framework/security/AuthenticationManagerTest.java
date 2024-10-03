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
package net.asany.jfantasy.framework.security;

import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authentication.UsernamePasswordAuthenticationToken;
import net.asany.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import net.asany.jfantasy.framework.security.crypto.password.PlaintextPasswordEncoder;
import org.junit.jupiter.api.Test;

class AuthenticationManagerTest {

  @Test
  void authenticate() {
    AuthenticationManager manager = new AuthenticationManager();

    DaoAuthenticationProvider provider =
        new DaoAuthenticationProvider(
            new SimpleUserDetailsService(),
            new PlaintextPasswordEncoder(),
            null,
            false,
            null,
            null);

    manager.addProvider(provider);

    Authentication authentication =
        new UsernamePasswordAuthenticationToken("", "limaofeng", "123456789");

    manager.authenticate(authentication);
  }
}
