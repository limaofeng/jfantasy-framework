package cn.asany.example.demo.domain;

import lombok.Data;

@Data
public class UserSetting implements IUserSettings {
  private String id = "123456";
  private String name = "limaofeng";
  private String favoriteColor = "blue";
}
