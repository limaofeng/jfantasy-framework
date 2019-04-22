package org.jfantasy.framework.dao.jpa;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jfantasy.framework.dao.BaseBusBusinessEntity;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.PropertyFilter;
import org.jfantasy.framework.spring.SpringContextUtil;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<T> findAll(List<PropertyFilter> filters) {
        return this.findAll(new PropertyFilterSpecification(this.getDomainClass(), filters));
    }

    @Override
    public List<T> findAll(List<PropertyFilter> filters, Sort sort) {
        return this.findAll(new PropertyFilterSpecification(this.getDomainClass(), filters), sort);
    }

    @Override
    public Pager<T> findPager(Pager<T> pager, List<PropertyFilter> filters) {
        return this.findPager(pager, new PropertyFilterSpecification(this.getDomainClass(), filters));
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
        if (BaseBusBusinessEntity.class.isAssignableFrom(this.getDomainClass())) {
            List<JpaRepository> repositories = Arrays.stream(SpringContextUtil.getApplicationContext().getBeanNamesForType(JpaRepository.class)).map(name -> SpringContextUtil.getBean(name, JpaRepository.class)).collect(Collectors.toList());
            ((BaseBusBusinessEntity) entity).setDeleted(true);
            List<FieldWarp> fields = Arrays.stream(ClassUtil.getDeclaredFields(this.getDomainClass())).filter(field -> {
                ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                boolean is = manyToMany != null && ObjectUtil.exists(manyToMany.cascade(), CascadeType.REMOVE);
                if (!is) {
                    OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                    is = oneToMany != null && ObjectUtil.exists(oneToMany.cascade(), CascadeType.REMOVE);
                }
                return is;
            }).map(field -> {
                Class entityType = ClassUtil.forName(RegexpUtil.parseGroup(field.getGenericType().getTypeName(), "<([^>]+)>", 1));
                Optional<JpaRepository> optional = repositories.stream().filter(dao -> entityType == ClassUtil.getInterfaceGenricType(dao.getClass().getInterfaces()[0], JpaRepository.class)).findAny();
                return FieldWarp.builder().field(field).domainClass(entityType).repository(optional.orElse(null)).build();
            }).collect(Collectors.toList());
            for (FieldWarp field : fields) {
                field.delete(entity);
            }
            ((BaseBusBusinessEntity) entity).setDeleted(true);
            this.save(entity);
        } else {
            super.delete(entity);
        }
    }

    @Override
    protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
        return super.getQuery(defaultSpecification(spec), domainClass, sort);
    }

    protected <S extends T> Specification<S> defaultSpecification(Specification<S> spec) {
        if (BaseBusBusinessEntity.class.isAssignableFrom(this.getDomainClass())) {
            if (spec == null) {
                spec = new ExcludeDeletedSpecification();
            } else {
                spec = spec.and(new ExcludeDeletedSpecification());
            }
        }
        return spec;
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class FieldWarp {
        private Field field;
        private Class domainClass;
        private JpaRepository repository;

        public Object getValue(Object entity) {
            OgnlUtil ognlUtil = OgnlUtil.getInstance();
            return ognlUtil.getValue(field.getName(), entity);
        }

        public void delete(Object entity) {
            if (ClassUtil.isList(field.getType())) {
                ((List) this.getValue(entity)).forEach(value -> {
                    repository.delete(value);
                });
            } else {
                repository.delete(this.getValue(entity));
            }
        }
    }


    public class ExcludeDeletedSpecification implements Specification {
        @Override
        public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder builder) {
            return builder.notEqual(root.get("deleted"), true);
        }
    }

}
