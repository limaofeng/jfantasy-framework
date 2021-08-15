package org.jfantasy.framework.lucene.dao.hibernate;

import java.util.ArrayList;
import java.util.List;
import org.jfantasy.framework.dao.jpa.JpaRepository;
import org.jfantasy.framework.lucene.backend.EntityChangedListener;
import org.jfantasy.framework.lucene.dao.LuceneDao;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class HibernateLuceneDao implements LuceneDao {

  private JpaRepository jpaRepository;
  private EntityChangedListener changedListener;

  public HibernateLuceneDao(JpaRepository jpaRepository) {
    this.jpaRepository = jpaRepository;
    this.changedListener = new EntityChangedListener(null);
  }

  @Override
  @Transactional(
      rollbackFor = Exception.class,
      readOnly = true,
      propagation = Propagation.NOT_SUPPORTED)
  public long count() {
    return jpaRepository.count();
  }

  @Override
  @Transactional(
      rollbackFor = Exception.class,
      readOnly = true,
      propagation = Propagation.NOT_SUPPORTED)
  public <T> List<T> find(final int start, final int size) {
    return new ArrayList<>();
  }

  @Override
  @Transactional(
      rollbackFor = Exception.class,
      readOnly = true,
      propagation = Propagation.NOT_SUPPORTED)
  public <T> List<T> findByField(final String fieldName, final String fieldValue) {
    return new ArrayList<>();
  }

  @Override
  @Transactional(
      rollbackFor = Exception.class,
      readOnly = true,
      propagation = Propagation.NOT_SUPPORTED)
  public <T> T getById(final String id) {
    return null;
  }

  @Override
  public EntityChangedListener getLuceneListener() {
    return changedListener;
  }
}
