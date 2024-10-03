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
package net.asany.jfantasy.framework.spring.validation;

/**
 * 验证接口
 *
 * @author limaofeng
 * @param <T>
 */
public interface Validator<T> {

  /**
   * 验证方法
   *
   * @param value 需要验证的值
   * @throws ValidationException 验证错误抛出的异常
   */
  void validate(T value) throws ValidationException;
}
