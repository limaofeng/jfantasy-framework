package cn.asany.demo.domain;

import org.jfantasy.framework.search.annotations.IndexRefBy;

public class User {

  @IndexRefBy(Article.class)
  private String name;
}
