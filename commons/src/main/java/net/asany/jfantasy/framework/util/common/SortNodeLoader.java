package net.asany.jfantasy.framework.util.common;

import java.io.Serializable;
import java.util.List;

public interface SortNodeLoader<T extends SortNode> {
  List<T> getAll(Serializable parentId, T moveNode);

  T load(Serializable id);
}
