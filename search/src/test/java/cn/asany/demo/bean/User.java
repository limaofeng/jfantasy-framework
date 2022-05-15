package cn.asany.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jfantasy.framework.search.annotations.Field;
import org.jfantasy.framework.search.annotations.Document;

@Document(fetcher = UserService.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @Field
  private Long id;

  @Field
  private String name;

  @Field
  private int age;
}
