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
