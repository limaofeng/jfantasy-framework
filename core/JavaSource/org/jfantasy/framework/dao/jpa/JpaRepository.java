package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 14/11/2017 12:55 PM
 */
@NoRepositoryBean
public interface JpaRepository<T, PK extends Serializable>  extends org.springframework.data.jpa.repository.JpaRepository<T,PK>, JpaSpecificationExecutor<T> {

    /**
     * 分页查询
     * @param pager
     * @param filters
     * @return
     */
    Pager<T> findPager(Pager<T> pager,List<PropertyFilter> filters);

}
