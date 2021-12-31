package org.jfantasy.framework.dao.jpa;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jfantasy.framework.dao.BaseBusBusinessEntity;
import org.jfantasy.framework.dao.LimitPageRequest;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.dao.hibernate.util.HibernateUtils;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.BeanUtil;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.toys.CompareResults;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadataUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 自己封装的 JpaRepository
 *
 * @author limaofeng
 * @version V1.0
 * @date 14/11/2017 11:23 AM
 */
@Slf4j
public class ComplexJpaRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
    implements JpaRepository<T, ID> {

  public static int BATCH_SIZE = 500;

  protected EntityManager em;
  protected JpaEntityInformation<T, ?> entityInformation;
  private static final Map<Class, JpaRepository> REPOSITORIES = new HashMap<>();

  public ComplexJpaRepository(Class<T> domainClass, EntityManager entityManager) {
    this(
        JpaEntityInformationSupport.getEntityInformation(domainClass, entityManager),
        entityManager);
    setRepositoryMethodMetadata(CrudMethodMetadataUtils.getCrudMethodMetadata());
  }

  @Autowired(required = false)
  public ComplexJpaRepository(
      JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.em = entityManager;
    this.entityInformation = entityInformation;
  }

  @Override
  public JpaEntityInformation getJpaEntityInformation() {
    return ClassUtil.getFieldValue(this, this.getClass(), "entityInformation");
  }

  @Override
  public List<T> findAll(List<PropertyFilter> filters) {
    return this.findAll(toSpecification(filters));
  }

  @Override
  public Optional<T> findOne(List<PropertyFilter> filters) {
    return this.findOne(toSpecification(filters));
  }

  @Override
  public boolean exists(List<PropertyFilter> filters) {
    return count(filters) > 0;
  }

  @Override
  public Optional<T> findOneBy(String name, Object value) {
    return this.findOne(PropertyFilter.builder().equal(name, value).build());
  }

  protected Specification<T> toSpecification(List<PropertyFilter> filters) {
    return new PropertyFilterSpecification<T>(this.getDomainClass(), filters);
  }

  @Override
  public long count(List<PropertyFilter> filters) {
    return this.count(toSpecification(filters));
  }

  @Override
  public List<T> findAll(List<PropertyFilter> filters, Sort sort) {
    return this.findAll(toSpecification(filters), sort);
  }

  @Override
  public List<T> findAll(List<PropertyFilter> filters, int size) {
    return super.getQuery(toSpecification(filters), Sort.unsorted())
        .setMaxResults(size)
        .getResultList();
  }

  @Override
  public List<T> findAll(List<PropertyFilter> filters, int size, Sort sort) {
    return super.getQuery(toSpecification(filters), sort).setMaxResults(size).getResultList();
  }

  @Override
  public Pager<T> findPager(Pager<T> pager, List<PropertyFilter> filters) {
    return this.findPager(pager, toSpecification(filters));
  }

  @Override
  public Pager<T> findPager(Pager<T> pager, Specification<T> spec) {
    Pageable pageRequest;
    if (pager.getFirst() == 0) {
      pager.reset((int) this.count(spec));
      pageRequest =
          PageRequest.of(pager.getCurrentPage() - 1, pager.getPageSize(), pager.getSort());
    } else {
      pager.setTotalCount((int) this.count(spec));
      pageRequest = LimitPageRequest.of(pager.getFirst(), pager.getPageSize(), pager.getSort());
    }
    Page<T> page = this.findAll(spec, pageRequest);
    pager.reset((int) page.getTotalElements(), page.getContent());
    return pager;
  }

  @Override
  public T update(T entity) {
    return super.save(entity);
  }

  @Override
  public T update(T entity, boolean merge) {
    if (merge) {
      T oldEntity = super.getById(getIdValue(entity));
      if (entity == oldEntity) {
        return this.save(entity);
      }
      return super.save(merge(entity, oldEntity, this.getDomainClass(), OgnlUtil.getInstance()));
    } else {
      return this.update(entity);
    }
  }

  private ID getIdValue(T entity) {
    ID id = HibernateUtils.getIdValue(this.getDomainClass(), entity);
    assert id != null;
    return id;
  }

  @Override
  public <S extends T> Iterable<S> saveAllInBatch(Iterable<S> entities) {
    Iterator<S> iterator = entities.iterator();
    int index = 0;
    while (iterator.hasNext()) {
      em.persist(iterator.next());
      index++;
      if (index % BATCH_SIZE == 0) {
        em.flush();
        em.clear();
      }
    }
    if (index % BATCH_SIZE != 0) {
      em.flush();
      em.clear();
    }
    return entities;
  }

  @Override
  public <S extends T> Iterable<S> updateAllInBatch(Iterable<S> entities) {
    Iterator<S> iterator = entities.iterator();
    int index = 0;
    while (iterator.hasNext()) {
      em.merge(iterator.next());
      index++;
      if (index % BATCH_SIZE == 0) {
        em.flush();
        em.clear();
      }
    }
    if (index % BATCH_SIZE != 0) {
      em.flush();
      em.clear();
    }
    return entities;
  }

  private <O> O merge(Object entity, Object oldEntity, Class entityClass, OgnlUtil ognlUtil) {
    // 为普通字段做值转换操作
    this.cleanColumn(
        entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, Column.class), ognlUtil);
    // 一对一关联关系的表
    this.cleanOneToOne(
        entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, OneToOne.class), ognlUtil);
    // 多对一关联关系的表
    this.cleanManyToOne(
        entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, ManyToOne.class), ognlUtil);
    // 多对多关联关系的表
    this.cleanManyToMany(
        entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, ManyToMany.class), ognlUtil);
    // 一对多关联关系的表
    this.cleanOneToMany(
        entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, OneToMany.class), ognlUtil);
    return (O) oldEntity;
  }

  private void cleanColumn(Object entity, Object oldEntity, Field[] fields, OgnlUtil ognlUtil) {
    for (Field field : fields) {
      Object value = ognlUtil.getValue(field.getName(), entity);
      if (value != null) {
        ClassUtil.setValue(oldEntity, field.getName(), value);
      }
    }
  }

  private void cleanOneToOne(Object entity, Object oldEntity, Field[] fields, OgnlUtil ognlUtil) {
    for (Field field : fields) {
      OneToOne oneToOne = field.getAnnotation(OneToOne.class);
      if (!(ObjectUtil.indexOf(oneToOne.cascade(), CascadeType.ALL) > -1
          || ObjectUtil.indexOf(oneToOne.cascade(), CascadeType.MERGE) > -1)) {
        continue;
      }
      Object value = ClassUtil.getValue(entity, field.getName());
      if (value == null) {
        continue;
      }
      Object oldValue = ClassUtil.getValue(oldEntity, field.getName());
      if (oldValue == null) {
        ClassUtil.setValue(oldEntity, field.getName(), value);
      } else {
        merge(value, oldValue, ClassUtil.getRealClass(field.getType()), ognlUtil);
      }
    }
  }

  private void cleanManyToOne(
      Object entity, Object oldEntity, Field[] manyToOneFields, OgnlUtil ognlUtil) {
    for (Field field : manyToOneFields) {
      Object fk = ognlUtil.getValue(field.getName(), entity);
      if (fk == null) {
        ognlUtil.setValue(field.getName(), entity, ognlUtil.getValue(field.getName(), oldEntity));
        continue;
      }
      Serializable fkId = HibernateUtils.getIdValue(field.getType(), fk);
      Object fkObj = fkId != null ? getJpaRepository(field.getType()).getById(fkId) : null;
      ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, fkObj);
    }
  }

  private void cleanManyToMany(
      Object entity, Object oldEntity, Field[] manyToManyFields, OgnlUtil ognlUtil) {
    for (Field field : manyToManyFields) {
      ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
      Class targetEntityClass = manyToMany.targetEntity();
      if (void.class == targetEntityClass) {
        targetEntityClass = ClassUtil.getFieldGenericType(field);
      }
      Object fks = ognlUtil.getValue(field.getName(), entity);
      if (!ClassUtil.isList(fks)) {
        continue;
      }

      List<Object> source = ognlUtil.getValue(field.getName(), oldEntity);
      List<Object> objects = (List<Object>) fks;

      if (source == objects) {
        continue;
      }

      if (source != null && !source.isEmpty()) {
        Class finalTargetEntityClass = targetEntityClass;
        CompareResults results =
            ObjectUtil.compare(
                source,
                objects,
                (Object o1, Object o2) -> {
                  Serializable fkId1 = HibernateUtils.getIdValue(finalTargetEntityClass, o1);
                  Serializable fkId2 = HibernateUtils.getIdValue(finalTargetEntityClass, o2);
                  return (fkId1 == fkId2) ? 0 : -1;
                });

        ObjectUtil.remove(source, (item) -> results.getExceptA().contains(item));
        source.addAll((Collection<?>) results.getExceptB());
      } else {
        List<Object> addObjects = new ArrayList<>();
        for (Object fk : objects) {
          Serializable fkId = HibernateUtils.getIdValue(targetEntityClass, fk);
          Object fkObj = fkId != null ? getJpaRepository(targetEntityClass).getById(fkId) : null;
          if (fkObj != null) {
            addObjects.add(fkObj);
          }
        }
        ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, addObjects);
      }
    }
  }

  private void cleanOneToMany(
      Object entity, Object oldEntity, Field[] oneToManyFields, OgnlUtil ognlUtil) {
    for (Field field : oneToManyFields) {
      OneToMany oneToMany = field.getAnnotation(OneToMany.class);
      Class targetEntityClass = oneToMany.targetEntity();
      if (void.class == targetEntityClass) {
        targetEntityClass = ClassUtil.getFieldGenericType(field);
      }
      if (oneToMany.cascade().length != 0
          && !(ObjectUtil.indexOf(oneToMany.cascade(), CascadeType.ALL) > -1
              || ObjectUtil.indexOf(oneToMany.cascade(), CascadeType.MERGE) > -1)) {
        continue;
      }
      Object fks = ognlUtil.getValue(field.getName(), entity);
      if (ClassUtil.isList(fks)) {
        List<Object> objects = (List<Object>) fks;
        List<Object> addObjects = new ArrayList<>();
        for (Object fk : objects) {
          Serializable fkId = HibernateUtils.getIdValue(targetEntityClass, fk);
          Object fkObj = fkId != null ? getJpaRepository(targetEntityClass).getById(fkId) : null;
          if (fkObj != null) {
            addObjects.add(BeanUtil.copyProperties(fkObj, fk));
          } else {
            addObjects.add(fk);
          }
        }
        ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, addObjects);
        if (oldEntity == null) {
          continue;
        }
        List<Object> oldFks = ognlUtil.getValue(field.getName(), oldEntity);
        // 删除原有数据
        for (Object odl : oldFks) {
          if (ObjectUtil.find(
                  addObjects,
                  this.getIdName(targetEntityClass),
                  HibernateUtils.getIdValue(targetEntityClass, odl))
              == null) {
            getJpaRepository(targetEntityClass).delete(odl);
            log.debug("删除数据" + HibernateUtils.getIdValue(targetEntityClass, odl));
          }
        }
      }
    }
  }

  public String getIdName(Class entityClass) {
    JpaRepository repository = getJpaRepository(entityClass);
    JpaEntityInformation<T, ?> entityInformation = repository.getJpaEntityInformation();
    return entityInformation.getIdAttribute().getName();
  }

  public JpaRepository getJpaRepository(Class domainClass) {
    if (REPOSITORIES.isEmpty()) {
      Arrays.stream(
              SpringBeanUtils.getApplicationContext().getBeanNamesForType(JpaRepository.class))
          .map(name -> SpringBeanUtils.getBean(name, JpaRepository.class))
          .forEach(
              repository -> {
                Class entityType =
                    ClassUtil.getInterfaceGenricType(
                        repository.getClass().getInterfaces()[0], JpaRepository.class);
                REPOSITORIES.put(entityType, repository);
              });
    }
    return REPOSITORIES.get(domainClass);
  }

  @Override
  public void delete(T entity) {
    if (BaseBusBusinessEntity.class.isAssignableFrom(this.getDomainClass())) {
      ((BaseBusBusinessEntity) entity).setDeleted(true);
      List<FieldWarp> fields =
          Arrays.stream(ClassUtil.getDeclaredFields(this.getDomainClass()))
              .filter(
                  field -> {
                    ManyToMany manyToMany = field.getAnnotation(ManyToMany.class);
                    boolean is =
                        manyToMany != null
                            && ObjectUtil.exists(manyToMany.cascade(), CascadeType.REMOVE);
                    if (!is) {
                      OneToMany oneToMany = field.getAnnotation(OneToMany.class);
                      is =
                          oneToMany != null
                              && ObjectUtil.exists(oneToMany.cascade(), CascadeType.REMOVE);
                    }
                    return is;
                  })
              .map(
                  field -> {
                    Class entityType =
                        ClassUtil.forName(
                            RegexpUtil.parseGroup(
                                field.getGenericType().getTypeName(), "<([^>]+)>", 1));
                    JpaRepository repository = getJpaRepository(entityType);
                    return FieldWarp.builder()
                        .field(field)
                        .domainClass(entityType)
                        .repository(repository)
                        .build();
                  })
              .collect(Collectors.toList());
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
  protected <S extends T> TypedQuery<S> getQuery(
      @Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
    return super.getQuery(defaultSpecification(spec), domainClass, sort);
  }

  @Override
  protected <S extends T> TypedQuery<Long> getCountQuery(
      @Nullable Specification<S> spec, Class<S> domainClass) {
    return super.getCountQuery(defaultSpecification(spec), domainClass);
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
    private JpaRepository<Object, Serializable> repository;

    public Object getValue(Object entity) {
      OgnlUtil ognlUtil = OgnlUtil.getInstance();
      return ognlUtil.getValue(field.getName(), entity);
    }

    public void delete(Object entity) {
      if (ClassUtil.isList(field.getType())) {
        ((List) this.getValue(entity))
            .forEach(
                value -> {
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

  public Pager<T> findPager(Pager<T> pager, String hql, Object... values) {
    Query q = createQuery(hql, values);
    pager.setTotalCount(countHqlResult(hql, values));
    setPageParameter(q, pager);
    pager.reset(q.getResultList());
    return pager;
  }

  public Pager<T> findPager(Pager<T> pager, String hql, Map<String, ?> values) {
    Query q = createQuery(hql, values);
    pager.setTotalCount(countHqlResult(hql, values));
    setPageParameter(q, pager);
    pager.reset(q.getResultList());
    return pager;
  }

  protected <C> Query setPageParameter(Query q, Pager<C> pagination) {
    q.setFirstResult(pagination.getFirst());
    q.setMaxResults(pagination.getPageSize());
    return q;
  }

  protected String createCountHQL(String hql) {
    String fromHql = hql;
    fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
    fromHql = StringUtils.substringBefore(fromHql, "order by");
    return "select count(*) " + fromHql;
  }

  @SneakyThrows
  protected int countHqlResult(String hql, Object... values) {
    Long count = (Long) findUnique(createCountHQL(hql), values);
    return count.intValue();
  }

  @SneakyThrows
  protected int countHqlResult(String hql, Map<String, ?> values) {
    Long count = (Long) findUnique(createCountHQL(hql), values);
    return count.intValue();
  }

  /**
   * 使用hql查询对象,推荐使用 {@link @HibernateDao.find(String,Map<String, ?>)}
   *
   * @param hql hql语句
   * @param values 参数
   * @return 返回集合
   */
  public List<T> find(String hql, Object... values) {
    return createQuery(hql, values).getResultList();
  }

  /**
   * 使用hql查询对象
   *
   * @param hql hql语句
   * @param values 参数
   * @return 返回集合
   */
  public List<T> find(String hql, Map<String, ?> values) {
    return createQuery(hql, values).getResultList();
  }

  public T findUnique(String hql, Object... values) {
    return (T) createQuery(hql, values).getSingleResult();
  }

  public T findUnique(String hql, Map<String, ?> values) {
    return (T) createQuery(hql, values).getSingleResult();
  }

  public int batchExecute(String hql, Object... values) {
    return createQuery(hql, values).executeUpdate();
  }

  public int batchExecute(String hql, Map<String, ?> values) {
    return createQuery(hql, values).executeUpdate();
  }

  public int batchSQLExecute(String sql, Object... values) {
    return createSQLQuery(sql, values).executeUpdate();
  }

  public int batchSQLExecute(String sql, Map<String, ?> values) {
    return createSQLQuery(sql, values).executeUpdate();
  }

  protected Query createQuery(String hql, Object... values) {
    Assert.hasText("hql 不能为空", hql);
    Query query = em.createQuery(hql, entityInformation.getJavaType());
    for (int i = 0; i < values.length; i++) {
      query.setParameter(i, values[i]);
    }
    return query;
  }

  protected Query createQuery(String hql, Map<String, ?> values) {
    Assert.hasText(hql, "hql 不能为空");
    Query query = em.createQuery(hql, entityInformation.getJavaType());
    for (Map.Entry<String, ?> entry : values.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
    return query;
  }

  protected Query createSQLQuery(String sql, Object... values) {
    Assert.hasText(sql, "sql 不能为空");
    Query query = em.createNativeQuery(sql, entityInformation.getJavaType());
    for (int i = 0; i < values.length; i++) {
      query.setParameter(i, values[i]);
    }
    return query;
  }

  protected Query createSQLQuery(String sql, Map<String, ?> values) {
    Assert.hasText(sql, "sql 不能为空");
    Query query = em.createNativeQuery(sql, entityInformation.getJavaType());
    for (Map.Entry<String, ?> entry : values.entrySet()) {
      query.setParameter(entry.getKey(), entry.getValue());
    }
    return query;
  }
}
