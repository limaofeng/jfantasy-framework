package net.asany.jfantasy.graphql;

/**
 * 边
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-02 18:23
 */
public interface Edge<T> {
  /**
   * 游标
   *
   * @return String
   */
  String getCursor();

  /**
   * 当前数据
   *
   * @return T
   */
  T getNode();

  /**
   * 设置游标
   *
   * @param cursor String
   */
  void setCursor(String cursor);

  /**
   * 设置数据
   *
   * @param node T
   */
  void setNode(T node);
}
