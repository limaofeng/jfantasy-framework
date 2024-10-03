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
package net.asany.jfantasy.framework.util.userstamp;

import lombok.Getter;

public class UserStamp {
  @Getter private int randomType;
  @Getter private String passwordHash;
  private String str;

  @Override
  public String toString() {
    return this.str;
  }

  protected void setRandomType(int randomType) {
    this.randomType = randomType;
  }

  protected void setStr(String str) {
    this.str = str;
  }

  protected void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }
}
