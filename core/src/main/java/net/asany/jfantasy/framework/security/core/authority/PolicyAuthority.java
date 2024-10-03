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
package net.asany.jfantasy.framework.security.core.authority;

import net.asany.jfantasy.framework.security.core.GrantedAuthority;

public class PolicyAuthority implements GrantedAuthority {

  public static final String TYPE = "POLICY";
  private final String policy;

  public PolicyAuthority(String policy) {
    this.policy = policy;
  }

  @Override
  public String getAuthority() {
    return TYPE + "_" + policy;
  }

  public String getPolicy() {
    return this.policy;
  }
}
