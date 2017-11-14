package org.jfantasy.framework.dao.jpa;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 14/11/2017 12:55 PM
 */
@NoRepositoryBean
public interface JpaRepository<T, PK extends Serializable>  extends org.springframework.data.jpa.repository.JpaRepository<T,PK>, JpaSpecificationExecutor<T> {

}
