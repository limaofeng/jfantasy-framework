package org.jfantasy.framework.util.common.dto;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString(exclude = "children")
public class TreeNode {
  private String id;
  private String name;
  private TreeNode parent;
  private int index;
  private List<TreeNode> children;
}
