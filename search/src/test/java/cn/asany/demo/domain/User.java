package cn.asany.demo.domain;

import net.asany.jfantasy.framework.search.annotations.IndexRefBy;

public class User {

  @IndexRefBy(Article.class)
  private String name;
}
