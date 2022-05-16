package org.jfantasy.framework.search.dao;

import org.jfantasy.framework.search.backend.EntityChangedListener;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.provider.PersistenceProvider;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;

import static org.springframework.data.jpa.repository.query.QueryUtils.COUNT_QUERY_STRING;
import static org.springframework.data.jpa.repository.query.QueryUtils.getQueryString;

public class JpaDefaultDataFetcher implements DataFetcher {

  protected EntityManager em;

  protected JpaEntityInformation entityInformation;
  private final Class domainClass;
  private final ApplicationContext applicationContext;
  private final PersistenceProvider provider;

  public JpaDefaultDataFetcher(ApplicationContext applicationContext, Class domainClass) {
    this.domainClass = domainClass;
    this.applicationContext = applicationContext;
    this.em = this.applicationContext.getBean(EntityManager.class);
    this.entityInformation = JpaEntityInformationSupport.getEntityInformation(domainClass, this.em);
    this.provider = PersistenceProvider.fromEntityManager(this.em);
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
  public EntityChangedListener getListener() {
    return null;
  }
}
