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
package net.asany.jfantasy.framework.security.authorization.policy;

/**
 * 策略生效的效果
 *
 * @author limaofeng
 */
public enum PolicyEffect {
  /** 允许 */
  ALLOW,
  /** 拒绝 */
  DENY;

  public static PolicyEffect fromString(String text) {
    for (PolicyEffect effect : PolicyEffect.values()) {
      if (effect.name().equalsIgnoreCase(text)) {
        return effect;
      }
    }
    throw new IllegalArgumentException("No constant with text " + text + " found");
  }

  public boolean isAllow() {
    return this == ALLOW;
  }
}
