package cn.asany.example.demo.graphql.inputs;

import lombok.Data;

@Data
public class UserCreateInput {
  private String username;
  private String password;
}
