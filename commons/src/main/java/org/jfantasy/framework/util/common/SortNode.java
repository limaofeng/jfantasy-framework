package org.jfantasy.framework.util.common;

import java.io.Serializable;

public interface SortNode {

  Serializable getId();

  Serializable getParentId();

  Integer getIndex();

  Integer getLevel();

  void setLevel(Integer level);

  void setIndex(Integer index);
}
