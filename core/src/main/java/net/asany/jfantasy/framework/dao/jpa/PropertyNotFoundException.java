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
package net.asany.jfantasy.framework.dao.jpa;

import net.asany.jfantasy.framework.error.ValidationException;

/**
 * 字段未发现
 *
 * @author limaofeng
 */
public class PropertyNotFoundException extends ValidationException {
  public PropertyNotFoundException(String name) {
    super("过滤字段: " + name + " 不存在");
  }

  public PropertyNotFoundException(String name, String message) {
    super("过滤字段：" + name + ", " + message);
  }
}
