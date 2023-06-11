package cn.asany.example.demo.graphql.inputs;

import cn.asany.example.demo.domain.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jfantasy.graphql.inputs.WhereInput;

/** @author limaofeng */
@Data
@EqualsAndHashCode(callSuper = false)
public class UserWhereInput extends WhereInput<UserWhereInput, User> {

  @JsonProperty("username_contains")
  public void setUsernameContains(String value) {
    filter.contains("username", value);
  }
}
