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
package net.asany.jfantasy.framework.spring.mvc.error;

import org.springframework.http.HttpStatus;

/**
 * @author limaofeng
 */
public class SecurityException extends RestException {

  public SecurityException(String code, String message) {
    super(HttpStatus.FORBIDDEN.value(), code, message);
  }

  public SecurityException(String message) {
    super(message);
  }
}
