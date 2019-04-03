package org.jfantasy.framework.dao.jpa;

import org.jfantasy.framework.dao.BaseBusBusinessEntity;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: 自己封装的 JpaRepository
 * @date 14/11/2017 11:23 AM
 */
public class ComplexJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements JpaRepository<T, ID> {

    @Autowired(required = false)
    public ComplexJpaRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
    }

    @Override
    public Pager<T> findPager(Pager<T> pager, List<PropertyFilter> filters) {
        return this.findPager(pager, new PropertyFilterSpecification(filters));
    }

    @Override
    public Pager<T> findPager(Pager<T> pager, Specification<T> spec) {
        PageRequest pageRequest = PageRequest.of(pager.getCurrentPage() - 1, pager.getPageSize(), pager.getSort());
        Page<T> page = this.findAll(spec, pageRequest);
        pager.reset(Long.valueOf(page.getTotalElements()).intValue(), page.getContent());
        return pager;
    }

    @Override
    public void delete(T entity) {
        if (entity instanceof BaseBusBusinessEntity) {
            ((BaseBusBusinessEntity) entity).setDeleted(true);
            Field[] fields = ObjectUtil.filter(ClassUtil.getDeclaredFields(this.getDomainClass()), field -> {
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                boolean is = manyToMany != null && ObjectUtil.exists(manyToMany.cascade(), CascadeType.REMOVE);
                if (!is) {
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    is = oneToMany != null && ObjectUtil.exists(oneToMany.cascade(), CascadeType.REMOVE);
                }
                return is;
            });
            // 递归查询子对象 - 并执行更新逻辑
        } else {
            super.delete(entity);
        }
    }

}
