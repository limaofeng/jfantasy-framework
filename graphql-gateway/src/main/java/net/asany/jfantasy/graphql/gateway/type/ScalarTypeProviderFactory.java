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
package net.asany.jfantasy.graphql.gateway.type;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScalarTypeProviderFactory {
  private final Map<String, ScalarTypeProvider> providers;

  @Getter
  private final ScalarTypeProvider defaultScalarTypeProvider = new DefaultScalarTypeProvider();

  public ScalarTypeProviderFactory() {
    providers = new HashMap<>();
    // 注册所有已知的ScalarTypeProvider实现
    providers.put("extended-scalars", new ExtendedScalarsTypeProvider());
  }

  /**
   * 注册新的ScalarTypeProvider。
   *
   * @param key 标识符（通常是provider的名称）
   * @param provider ScalarTypeProvider的实例
   */
  public void registerProvider(String key, ScalarTypeProvider provider) {
    providers.put(key, provider);
  }

  public ScalarTypeProvider getProvider(String name) {
    if (name == null) {
      return this.defaultScalarTypeProvider;
    }
    ScalarTypeProvider provider = providers.get(name);
    if (provider == null) {
      log.warn("No provider found for: " + name);
      return defaultScalarTypeProvider;
    }
    return provider;
  }
}
