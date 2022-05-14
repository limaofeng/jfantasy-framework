package org.jfantasy.framework.search;

import java.util.HashMap;
import java.util.Map;

public class Document {
  private final Map<String, Object> attrs = new HashMap<>();

  public void setBoost(float fit) {}

  public void add(String name, Object value) {
    this.attrs.put(name, value);
  }
}
