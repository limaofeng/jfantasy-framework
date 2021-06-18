package org.jfantasy.framework.util.common.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

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
