package org.jfantasy.framework.dao.jpa;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import org.jfantasy.framework.dao.Pager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author limaofeng
 * @version V1.0
 * @date 14/11/2017 12:55 PM
 */
@NoRepositoryBean
public interface JpaRepository<T, PK extends Serializable>
    extends org.springframework.data.jpa.repository.JpaRepository<T, PK>,
        JpaSpecificationExecutor<T> {

  /**
   * 实体信息
   *
   * @return JpaEntityInformation
   */
  JpaEntityInformation getJpaEntityInformation();

  /**
   * 查询数据集
   *
   * @param filters 过滤器
   * @return List<T>
   */
  List<T> findAll(List<PropertyFilter> filters);

  /**
   * 查询数据集
   *
   * @param filters 过滤器
   * @param sort 排序
   * @return List<T>
   */
  List<T> findAll(List<PropertyFilter> filters, Sort sort);

  /**
   * 通过过滤器返回唯一对象
   *
   * @param filters 过滤器
   * @return Optional<T>
   */
  Optional<T> findOne(List<PropertyFilter> filters);

  /**
   * 判断数据是否存在
   *
   * @param filters 过滤器
   * @return boolean
   */
  boolean exists(List<PropertyFilter> filters);

  /**
   * 唯一字段查询
   *
   * @param name 字段名
   * @param value 字段值
   * @return Optional<T>
   */
  Optional<T> findBy(String name, Object value);

  /**
   * 数据集数量
   *
   * @param filters 过滤
   * @return long
   */
  long count(List<PropertyFilter> filters);

  /**
   * 分页查询
   *
   * @param pager 分页对象
   * @param filters 过滤
   * @return Pager<T>
   */
  Pager<T> findPager(Pager<T> pager, List<PropertyFilter> filters);

  /**
   * 分页查询
   *
   * @param pager 分页对象
   * @param spec Specification
   * @return Pager<T>
   */
  Pager<T> findPager(Pager<T> pager, Specification<T> spec);

  /**
   * 更新
   *
   * @param entity 实体
   * @return T
   */
  T update(T entity);

  /**
   * 更新
   *
   * @param entity 实体
   * @param merge 是否使用合并模式
   * @return T
   */
  T update(T entity, boolean merge);

  /**
   * 批量保存
   *
   * @param entities 实体集合
   * @param <S> 泛型
   * @return Iterable
   */
  <S extends T> Iterable<S> saveAllInBatch(Iterable<S> entities);

  /**
   * 批量更新
   *
   * @param entities 实体集合
   * @param <S> 泛型
   * @return Iterable
   */
  <S extends T> Iterable<S> updateAllInBatch(Iterable<S> entities);
}
