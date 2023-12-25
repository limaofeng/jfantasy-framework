package net.asany.jfantasy.framework.dao;

/**
 * 所有者标识
 *
 * @author limaofeng
 */
public interface Ownable {
  /**
   * 获取所有者
   *
   * @return 所有者
   */
  String getOwner();

  /**
   * 设置所有者
   *
   * @param owner 所有者
   */
  void setOwner(String owner);
}
