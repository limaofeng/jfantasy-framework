package net.asany.jfantasy.demo.bean;

import com.fasterxml.jackson.annotation.JsonBackReference;

public class Article {
  private String name;
  private String title;
  private User user;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  @JsonBackReference
  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }
}
