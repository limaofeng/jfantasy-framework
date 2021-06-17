package org.jfantasy.framework.util.common.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TreeNode {
    private String id;
    private String name;
    private TreeNode parent;
    private List<TreeNode> children;
}
