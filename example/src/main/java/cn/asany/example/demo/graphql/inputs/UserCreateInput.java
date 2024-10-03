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
package cn.asany.example.demo.graphql.inputs;

import cn.asany.example.demo.validator.CaseValidator;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import net.asany.jfantasy.framework.spring.validation.Use;

@Data
public class UserCreateInput {
  @NotBlank(message = "用户名不能为空")
  @Use(value = CaseValidator.class, message = "自定义的错误消息")
  private String username;

  @NotBlank(message = "密码不能为空")
  private String password;
}
