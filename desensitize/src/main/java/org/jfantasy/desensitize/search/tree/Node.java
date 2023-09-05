package org.jfantasy.desensitize.search.tree;

import java.util.HashMap;
import java.util.Map;
import lombok.Getter;

/**
 * 节点
 *
 * @author limaofeng
 */
public class Node {
  private final Map<String, Node> children = new HashMap<>(0);
  private boolean isEnd = false;
  @Getter private String word;
  @Getter private double level = 0.0D;

  public Node() {}

  public Node addChar(char c) {
    String cStr = String.valueOf(c);
    Node node = this.children.get(cStr);
    if (node == null) {
      node = new Node();
      this.children.put(cStr, node);
    }

    return node;
  }

  public Node findChar(char c) {
    String cStr = String.valueOf(c);
    return this.children.get(cStr);
  }

  public boolean isEnd() {
    return this.isEnd;
  }

  public void setEnd(boolean isEnd) {
    this.isEnd = isEnd;
  }

  public void setLevel(double level) {
    this.level = level;
  }

  public void setWord(String word) {
    this.word = word;
  }
}
