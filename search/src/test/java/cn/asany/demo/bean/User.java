package cn.asany.demo.bean;

import lombok.Data;
import org.jfantasy.framework.search.annotations.IndexProperty;
import org.jfantasy.framework.search.annotations.Indexed;

@Indexed
@Data
public class User {

  @IndexProperty private Long id;

  @IndexProperty private String name;

  @IndexProperty private String age;
}
