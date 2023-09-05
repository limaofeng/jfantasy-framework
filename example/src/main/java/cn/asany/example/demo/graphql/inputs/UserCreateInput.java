package cn.asany.example.demo.graphql.inputs;

import cn.asany.example.demo.validator.CaseValidator;
import javax.validation.constraints.NotBlank;
import lombok.Data;
import org.jfantasy.framework.spring.validation.Use;

@Data
public class UserCreateInput {
  @NotBlank(message = "用户名不能为空")
  @Use(value = CaseValidator.class, message = "自定义的错误消息")
  private String username;

  @NotBlank(message = "密码不能为空")
  private String password;
}
