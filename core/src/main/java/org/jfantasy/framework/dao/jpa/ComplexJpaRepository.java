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
import org.jfantasy.framework.dao.LogicalDeletion;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.CrudMethodMetadataUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.data.support.PageableExecutionUtils;
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
  public List<T> findAll(PropertyFilter filters) {
    return this.findAll(toSpecification(filters));
  }

  @Override
  public Optional<T> findOne(PropertyFilter filter) {
    return this.findOne(toSpecification(filter));
  }

  @Override
  public boolean exists(PropertyFilter filter) {
    return count(filter) > 0;
  }

  @Override
  public Optional<T> findOneBy(String name, Object value) {
    return this.findOne(PropertyFilter.newFilter().equal(name, value));
  }

  protected Specification<T> toSpecification(PropertyFilter filter) {
    return new PropertyFilterSpecification<>(this.getDomainClass(), filter.build());
  }

  @Override
  public long count(PropertyFilter filter) {
    return this.count(toSpecification(filter));
  }

  @Override
  public List<T> findAll(PropertyFilter filter, Sort sort) {
    return this.findAll(toSpecification(filter), sort);
  }

  @Override
  public List<T> findAll(PropertyFilter filter, int size) {
    TypedQuery<T> query = super.getQuery(toSpecification(filter), Sort.unsorted());
    if (size > 0) {
      query.setMaxResults(size);
    }
    return query.getResultList();
  }

  @Override
  public List<T> findAll(PropertyFilter filter, int size, Sort sort) {
    TypedQuery<T> query = super.getQuery(toSpecification(filter), sort);
    if (size > 0) {
      query.setMaxResults(size);
    }
    return query.getResultList();
  }

  @Override
  public List<T> findAll(PropertyFilter filter, int offset, int limit, Sort sort) {
    TypedQuery<T> query = super.getQuery(toSpecification(filter), sort);
    query.setFirstResult(offset);
    if (limit > 0) {
      query.setMaxResults(limit);
    }
    return query.getResultList();
  }

  @Override
  public Page<T> findPage(Pageable pageable, PropertyFilter filter) {
    return this.findPage(pageable, toSpecification(filter));
  }

  @Override
  public Page<T> findPage(Pageable pageable, Specification<T> spec) {
    return this.findAll(spec, pageable);
  }

  @Override
  public EntityManager getEntityManager() {
    return this.em;
  }

  @Override
  public T update(T entity) {
    return super.save(entity);
  }

  @Override
  public T update(T entity, boolean merge) {
    if (merge) {
      T oldEntity = super.getReferenceById(getIdValue(entity));
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
    this.cleanEmbedded(
        entity, oldEntity, ClassUtil.getDeclaredFields(entityClass, Embedded.class), ognlUtil);
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
      copy(entity, oldEntity, field, ognlUtil);
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
      Object fkObj = fkId != null ? getJpaRepository(field.getType()).getReferenceById(fkId) : null;
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
          Object fkObj =
              fkId != null ? getJpaRepository(targetEntityClass).getReferenceById(fkId) : null;
          if (fkObj != null) {
            addObjects.add(fkObj);
          }
        }
        ognlUtil.setValue(field.getName(), oldEntity == null ? entity : oldEntity, addObjects);
      }
    }
  }

  private void cleanEmbedded(
      Object entity, Object oldEntity, Field[] embeddedFields, OgnlUtil ognlUtil) {
    for (Field field : embeddedFields) {
      copy(entity, oldEntity, field, ognlUtil);
    }
  }

  private void copy(Object entity, Object oldEntity, Field field, OgnlUtil ognlUtil) {
    Object value = ClassUtil.getValue(entity, field.getName());
    if (value == null) {
      return;
    }
    Object oldValue = ClassUtil.getValue(oldEntity, field.getName());
    if (oldValue == null) {
      ClassUtil.setValue(oldEntity, field.getName(), value);
    } else {
      merge(value, oldValue, ClassUtil.getRealClass(field.getType()), ognlUtil);
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
          Object fkObj =
              fkId != null ? getJpaRepository(targetEntityClass).getReferenceById(fkId) : null;
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
    return Objects.requireNonNull(entityInformation.getIdAttribute()).getName();
  }

  public JpaRepository getJpaRepository(Class domainClass) {
    if (REPOSITORIES.isEmpty()) {
      Arrays.stream(
              SpringBeanUtils.getApplicationContext().getBeanNamesForType(JpaRepository.class))
          .map(name -> SpringBeanUtils.getBean(name, JpaRepository.class))
          .filter(Objects::nonNull)
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
    if (LogicalDeletion.class.isAssignableFrom(this.getDomainClass())) {
      ((LogicalDeletion) entity).setDeleted(true);
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
      ((LogicalDeletion) entity).setDeleted(true);
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
    if (LogicalDeletion.class.isAssignableFrom(this.getDomainClass())) {
      String fieldName = LogicalDeletion.getDeletedFieldName(this.getDomainClass());
      if (spec == null) {
        spec = new ExcludeDeletedSpecification(fieldName);
      } else {
        spec = spec.and(new ExcludeDeletedSpecification(fieldName));
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
        repository.deleteAll(((List) this.getValue(entity)));
      } else {
        repository.delete(this.getValue(entity));
      }
    }
  }

  public static class ExcludeDeletedSpecification implements Specification {

    private final String fieldName;

    public ExcludeDeletedSpecification(String fieldName) {
      this.fieldName = fieldName;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder builder) {
      return builder.notEqual(root.get(this.fieldName), true);
    }
  }

  public Page<T> findPage(Pageable pageable, String hql, Object... values) {
    Query query = createQuery(hql, values);

    if (pageable.isUnpaged()) {
      return new PageImpl<T>(query.getResultList());
    }

    if (pageable.isPaged()) {
      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
    }

    return PageableExecutionUtils.getPage(
        query.getResultList(), pageable, () -> countHqlResult(hql, values));
  }

  public Page<T> findPage(Pageable pageable, String hql, Map<String, ?> values) {
    Query query = createQuery(hql, values);

    if (pageable.isUnpaged()) {
      return new PageImpl<T>(query.getResultList());
    }

    if (pageable.isPaged()) {
      query.setFirstResult((int) pageable.getOffset());
      query.setMaxResults(pageable.getPageSize());
    }
    return PageableExecutionUtils.getPage(
        query.getResultList(), pageable, () -> countHqlResult(hql, values));
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
   * 使用hql查询对象
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
