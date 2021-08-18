package cn.asany.his.demo.graphql.inputs;

import cn.asany.his.demo.bean.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jfantasy.graphql.inputs.QueryFilter;

/** @author limaofeng */
@Data
@EqualsAndHashCode
public class UserFilter extends QueryFilter<UserFilter, User> {

  @JsonProperty("username_contains")
  public void setUsernameContains(String value) {
    builder.contains("username", value);
  }
}
