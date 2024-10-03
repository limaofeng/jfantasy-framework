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
package net.asany.jfantasy.framework.security.core.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import net.asany.jfantasy.framework.security.auth.core.ClientDetails;
import net.asany.jfantasy.framework.security.core.AbstractAuthenticatedPrincipal;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class ClientApp extends AbstractAuthenticatedPrincipal implements AuthenticatedPrincipal {

  private Long id;
  private String name;
  @JsonIgnore private ClientDetails clientDetails;

  public ClientApp(ClientDetails clientDetails) {
    this.id = clientDetails.getId();
    this.name = clientDetails.getName();
    this.clientDetails = clientDetails;
  }
}
