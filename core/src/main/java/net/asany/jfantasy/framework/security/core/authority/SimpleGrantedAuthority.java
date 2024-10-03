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

import lombok.*;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;

/**
 * 简单的授权
 *
 * @author limaofeng
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleGrantedAuthority implements GrantedAuthority {
  private String type;
  private String code;

  public SimpleGrantedAuthority(String authority) {
    int index = authority.indexOf("_");
    type = authority.substring(0, index);
    code = authority.substring(index + 1);
  }

  public static SimpleGrantedAuthority newInstance(String authority) {
    return new SimpleGrantedAuthority(authority);
  }

  @Override
  public String getAuthority() {
    return type + "_" + code;
  }

  @Override
  public String toString() {
    return getAuthority();
  }
}
