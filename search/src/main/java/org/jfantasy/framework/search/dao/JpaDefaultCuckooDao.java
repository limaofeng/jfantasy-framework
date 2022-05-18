package org.jfantasy.framework.search.dao;

import static org.springframework.data.jpa.repository.query.QueryUtils.COUNT_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;

import java.io.Serializable;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

public class JpaDefaultCuckooDao implements CuckooDao {

  private final EntityChangedListener changedListener;
  protected EntityManager em;

  protected JpaEntityInformation entityInformation;
  private final Class domainClass;
  protected final ApplicationContext applicationContext;
  private final PersistenceProvider provider;

  public JpaDefaultCuckooDao(
      ApplicationContext applicationContext, Class domainClass, TaskExecutor executor) {
    this.domainClass = domainClass;
    this.applicationContext = applicationContext;
    this.em = this.applicationContext.getBean(EntityManager.class);
    this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, this.em);
    this.provider = PersistenceProvider.fromEntityManager(this.em);
    this.changedListener = new EntityChangedListener(domainClass, executor);
  }

  @Override
  public long count() {
    String countQuery =
        String.format(COUNT_QUERY_STRING, provider.getCountQueryPlaceholder(), "%s");
    return em.createQuery(getQueryString(countQuery, entityInformation.getEntityName()), Long.class)
        .getSingleResult();
  }

  @Override
  public <T> List<T> find(int start, int size) {

    CriteriaBuilder builder = em.getCriteriaBuilder();
    CriteriaQuery<T> query = builder.createQuery(domainClass);

    Root<T> root = query.from(domainClass);
    query.select(root);

    return this.em.createQuery(query).setFirstResult(start).setMaxResults(size).getResultList();
  }

  @Override
  public <T> List<T> findByField(String fieldName, String fieldValue) {
    return null;
  }

  @Override
  public <T> T getById(Serializable id) {
    return em.find(getDomainClass(), id);
  }

  protected <T> Class<T> getDomainClass() {
    return entityInformation.getJavaType();
  }

  @Override
  public EntityChangedListener getEntityChangedListener() {
    return changedListener;
  }
}
