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
import org.hibernate.event.internal.DefaultSaveOrUpdateEventListener;
import org.hibernate.event.internal.EntityState;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.proxy.HibernateProxy;

/**
 * 使 GenericGenerator 注解支持非注解的字段生成
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-10-9 下午10:12:33
 */
public class PropertyGeneratorSaveOrUpdateEventListener extends DefaultSaveOrUpdateEventListener {

  private final transient IdentifierGeneratorFactory identifierGeneratorFactory;

  public PropertyGeneratorSaveOrUpdateEventListener(
      IdentifierGeneratorFactory identifierGeneratorFactory) {
    this.identifierGeneratorFactory = identifierGeneratorFactory;
  }

  @Override
  public void onSaveOrUpdate(SaveOrUpdateEvent event) {
    final SessionImplementor source = event.getSession();
    final Object object = event.getObject();
    final Object requestedId = event.getRequestedId();
    if (requestedId != null && object instanceof HibernateProxy) {
      ((HibernateProxy) object).getHibernateLazyInitializer().setIdentifier(requestedId);
    }
    if (!reassociateIfUninitializedProxy(object, source)) {
      final Object entity = source.getPersistenceContext().unproxyAndReassociate(object);
      EntityEntry entityEntry = source.getPersistenceContext().getEntry(entity);
      EntityState entityState =
          EntityState.getEntityState(
              entity,
              entity.getClass().getName(),
              entityEntry,
              event.getSession(),
              requestedId == null);
      IdentifierGeneratorUtil.initialize(
          entityState, event.getSession(), object, identifierGeneratorFactory);
    }
    super.onSaveOrUpdate(event);
  }
}
