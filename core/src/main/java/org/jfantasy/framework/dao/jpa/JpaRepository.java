package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.Pager;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 14/11/2017 12:55 PM
 */
@NoRepositoryBean
public interface JpaRepository<T, PK extends Serializable> extends org.springframework.data.jpa.repository.JpaRepository<T, PK>, JpaSpecificationExecutor<T> {

    JpaEntityInformation getJpaEntityInformation();

    List<T> findAll(List<PropertyFilter> filters);

    List<T> findAll(List<PropertyFilter> filters, Sort sort);

    Optional<T> findOne(List<PropertyFilter> filters);

    boolean exists(List<PropertyFilter> filters);

    Optional<T> findBy(String name, Object value);

    long count(List<PropertyFilter> filters);

    /**
     * 分页查询
     *
     * @param pager
     * @param filters
     * @return
     */
    Pager<T> findPager(Pager<T> pager, List<PropertyFilter> filters);

    Pager<T> findPager(Pager<T> pager, Specification<T> spec);

    <S extends T> S update(S entity);

    <S extends T> S update(S entity, boolean merge);

    <S extends T> Iterable<S> saveAllInBatch(Iterable<S> entities);

    <S extends T> Iterable<S> batchUpdate(Iterable<S> entities);
}
