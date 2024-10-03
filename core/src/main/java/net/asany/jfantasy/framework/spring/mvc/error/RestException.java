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

import java.io.Serializable;
import lombok.Getter;
import net.asany.jfantasy.framework.error.ValidationException;
import org.springframework.http.HttpStatus;

@Getter
public class RestException extends ValidationException {

  private int statusCode = HttpStatus.BAD_REQUEST.value();
  private Serializable state;

  public RestException(int statusCode, String message) {
    super(message);
    this.statusCode = statusCode;
  }

  public RestException(int statusCode, String code, String message) {
    super(code, message);
    this.statusCode = statusCode;
  }

  public RestException(String message) {
    super(message);
  }

  public void setState(Serializable state) {
    this.state = state;
  }
}
