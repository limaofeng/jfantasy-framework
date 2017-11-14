package org.jfantasy.framework.dao;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: 自己封装的 JpaRepository
 * @date 14/11/2017 11:23 AM
 */
public class ComplexJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>{


    public ComplexJpaRepository(JpaEntityInformation<T,?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

}
