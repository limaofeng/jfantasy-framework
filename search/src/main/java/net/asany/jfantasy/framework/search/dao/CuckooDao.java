package net.asany.jfantasy.framework.search.dao;

import java.io.Serializable;
import java.util.List;
import net.asany.jfantasy.framework.search.backend.EntityChangedListener;

public interface CuckooDao {

  /**
   * 返回全部数据条数
   *
   * @return long
   */
  long count();

  /**
   * 查询数据
   *
   * @param start 开始下标
   * @param size 返回数据条数
   * @return List
   * @param <T> 类型
   */
  <T> List<T> find(int start, int size);

  /**
   * @param fieldName 字段
   * @param fieldValue 字段值
   * @return List
   */
  <T> List<T> findByField(String fieldName, String fieldValue);

  /**
   * 通过 ID 获取数据
   *
   * @param id 主键
   * @return T
   * @param <T> 类型
   */
  <T> T getById(Serializable id);

  /**
   * 返回实体改变监听器
   *
   * @return EntityChangedListener
   */
  EntityChangedListener getEntityChangedListener();
}
