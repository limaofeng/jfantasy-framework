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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.asany.jfantasy.framework.error.ErrorResponse;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/22 4:43 下午
 */
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties("status")
public class WebError extends ErrorResponse {

  /** 对应浏览器状态 */
  private int status;

  private final String path;

  public WebError(Map<String, Object> error) {
    this.path = "path";
  }
}
