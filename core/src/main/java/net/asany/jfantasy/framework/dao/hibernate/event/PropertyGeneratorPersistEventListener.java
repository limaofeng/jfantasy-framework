/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.dao.hibernate.event;

import net.asany.jfantasy.framework.dao.hibernate.spi.IdentifierGeneratorUtil;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.event.internal.DefaultPersistEventListener;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.spi.PersistContext;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.id.factory.IdentifierGeneratorFactory;

public class PropertyGeneratorPersistEventListener extends DefaultPersistEventListener {

  private final transient IdentifierGeneratorFactory identifierGeneratorFactory;

  public PropertyGeneratorPersistEventListener(
      IdentifierGeneratorFactory identifierGeneratorFactory) {
    this.identifierGeneratorFactory = identifierGeneratorFactory;
  }

  @Override
  public void onPersist(PersistEvent event, PersistContext createCache) {
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
