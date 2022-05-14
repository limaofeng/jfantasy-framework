package cn.asany.demo.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jfantasy.framework.search.annotations.IndexProperty;
import org.jfantasy.framework.search.annotations.Indexed;

@Indexed(fetcher = UserService.class)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

  @IndexProperty private Long id;

  @IndexProperty private String name;

  @IndexProperty private int age;
}
