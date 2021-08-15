package org.jfantasy.framework.dao.hibernate.event;

import java.util.Map;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.internal.DefaultPersistEventListener;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.jfantasy.framework.dao.hibernate.spi.IdentifierGeneratorUtil;

public class PropertyGeneratorPersistEventListener extends DefaultPersistEventListener {

  private transient IdentifierGeneratorFactory identifierGeneratorFactory;

  public PropertyGeneratorPersistEventListener(
      IdentifierGeneratorFactory identifierGeneratorFactory) {
    this.identifierGeneratorFactory = identifierGeneratorFactory;
  }

  @Override
  public void onPersist(PersistEvent event, Map createCache) {
    final SessionImplementor source = event.getSession();
    final Object object = event.getObject();
    if (!IdentifierGeneratorUtil.reassociateIfUninitializedProxy(object, source)) {
      final Object entity = source.getPersistenceContext().unproxyAndReassociate(object);
      EntityEntry entityEntry = source.getPersistenceContext().getEntry(entity);
      EntityState entityState =
          EntityState.getEntityState(
              entity, entity.getClass().getName(), entityEntry, event.getSession(), true);
      IdentifierGeneratorUtil.initialize(
          entityState, event.getSession(), object, identifierGeneratorFactory);
    }
    super.onPersist(event, createCache);
  }
}
