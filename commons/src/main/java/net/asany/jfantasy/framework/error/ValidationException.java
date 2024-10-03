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
package net.asany.jfantasy.framework.error;

import java.util.Map;
import lombok.Getter;
import org.springframework.validation.BindingResult;

/**
 * 验证异常
 *
 * @author limaofeng
 */
@Getter
public class ValidationException extends RuntimeException {

  private String code;
  private BindingResult bindingResult;
  private transient Map<String, Object> data;

  public ValidationException(String code, String message) {
    super(message);
    this.code = code;
  }

  public ValidationException(BindingResult bindingResult) {
    super("输入的数据不合法,详情见 fields 字段");
    this.bindingResult = bindingResult;
  }

  public ValidationException(String message, Map<String, Object> data) {
    super(message);
    this.data = data;
  }

  public ValidationException(String code, String message, Map<String, Object> data) {
    super(message);
    this.code = code;
    this.data = data;
  }

  public ValidationException(String message) {
    super(message);
  }

  public boolean hasFieldErrors() {
    return this.bindingResult != null;
  }
}
