package net.asany.jfantasy.framework.error;

import com.fasterxml.jackson.annotation.JsonFilter;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonFilter("myFilter")
public class User {

  @Id private String id;
  @NotBlank private String name;

  private Integer age;
  private Date createDate;

  private String username;
  private boolean enabled;
  private String nickName;
}
