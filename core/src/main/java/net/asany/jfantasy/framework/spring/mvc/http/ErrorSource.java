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
package net.asany.jfantasy.framework.spring.mvc.http;

import lombok.Getter;
import net.asany.jfantasy.framework.error.ErrorResponse;

public class ErrorSource {

  @Getter private final ErrorResponse error;

  private final Object state;

  public ErrorSource(ErrorResponse error, Object state) {
    this.error = error;
    this.state = state;
  }

  public <T> T getState() {
    return state == null ? null : (T) state;
  }
}
