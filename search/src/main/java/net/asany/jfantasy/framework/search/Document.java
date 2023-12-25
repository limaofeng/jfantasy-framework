package net.asany.jfantasy.framework.search;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

public class Document {
  @Getter private final Map<String, Object> attrs = new HashMap<>();
  @Getter private final String indexName;
  @Getter @Setter private String id;

  public Document(String indexName) {
    this.indexName = indexName;
  }

  public void setBoost(float fit) {}

  public void add(String name, Object value) {
    this.attrs.put(name, value);
  }
}
