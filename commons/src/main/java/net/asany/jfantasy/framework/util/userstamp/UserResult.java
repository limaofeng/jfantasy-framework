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
import lombok.Setter;

@Getter
public class UserResult {
  private int userId;
  private String passwordHash;
  private int cssStyle;
  public static final int GUEST = 0;
  public static final int MEMBER = 1;
  public static final int COMPANY = 2;
  @Setter private int userType;
  @Setter private int randomType;

  public String getMemKey() {
    return (isCompany() ? "com" : isGuest() ? "guest" : "") + this.userId;
  }

  public boolean isGuest() {
    return this.userType == 0;
  }

  public boolean isMember() {
    return this.userType == 1;
  }

  public boolean isCompany() {
    return this.userType == 2;
  }

  public boolean checkPassword(String password) {
    String pwStamp = String.valueOf(Encoder.hashPassword(password));
    return pwStamp.equals(this.passwordHash);
  }

  protected void setUserId(int userId) {
    this.userId = userId;
  }

  protected void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  protected void setCssStyle(int cssStyle) {
    this.cssStyle = cssStyle;
  }
}
