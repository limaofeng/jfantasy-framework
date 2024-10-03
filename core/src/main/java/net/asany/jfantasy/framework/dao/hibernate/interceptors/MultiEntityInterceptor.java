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
package net.asany.jfantasy.framework.dao.hibernate.interceptors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.Transaction;
import org.hibernate.metamodel.spi.EntityRepresentationStrategy;
import org.hibernate.type.Type;

/**
 * hibernate Interceptor 多实现
 *
 * @author 李茂峰
 * @version 1.0 默认的hibernate拦截器只能配置一个
 * @since 2013-9-12 下午5:01:53
 */
public class MultiEntityInterceptor implements Interceptor {

  private final Set<Interceptor> interceptors = new HashSet<>();

  public void addInterceptors(Interceptor interceptor) {
    this.interceptors.add(interceptor);
  }

  @Override
  public void afterTransactionBegin(Transaction transaction) {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.afterTransactionBegin(transaction);
    }
  }

  @Override
  public void afterTransactionCompletion(Transaction transaction) {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.afterTransactionCompletion(transaction);
    }
  }

  @Override
  public void beforeTransactionCompletion(Transaction transaction) {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.beforeTransactionCompletion(transaction);
    }
  }

  @Override
  public int[] findDirty(
      Object entity,
      Serializable id,
      Object[] currentState,
      Object[] previousState,
      String[] propertyNames,
      Type[] types) {
    int[] retVal = new int[0];
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.findDirty(entity, id, currentState, previousState, propertyNames, types);
    }
    return retVal;
  }

  @Override
  public Object getEntity(String entityClass, Serializable id) throws CallbackException {
    Object retVal = null;
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.getEntity(entityClass, id);
    }
    return retVal;
  }

  @Override
  public String getEntityName(Object entity) throws CallbackException {
    String retVal = null;
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.getEntityName(entity);
    }
    return retVal;
  }

  @Override
  public Object instantiate(
      String entityName, EntityRepresentationStrategy representationStrategy, Object id)
      throws CallbackException {
    Object retVal = null;
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.instantiate(entityName, representationStrategy, id);
    }
    return retVal;
  }

  @Override
  public Boolean isTransient(Object entity) {
    Boolean retVal = null;
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.isTransient(entity);
    }
    return retVal;
  }

  @Override
  public void onCollectionRecreate(Object entity, Serializable id) throws CallbackException {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.onCollectionRecreate(entity, id);
    }
  }

  @Override
  public void onCollectionRemove(Object entity, Serializable id) throws CallbackException {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.onCollectionRemove(entity, id);
    }
  }

  @Override
  public void onCollectionUpdate(Object entity, Serializable id) throws CallbackException {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.onCollectionUpdate(entity, id);
    }
  }

  @Override
  public void onDelete(
      Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
      throws CallbackException {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.onDelete(entity, id, state, propertyNames, types);
    }
  }

  @Override
  public boolean onFlushDirty(
      Object entity,
      Serializable id,
      Object[] currentState,
      Object[] previousState,
      String[] propertyNames,
      Type[] types)
      throws CallbackException {
    boolean retVal = true;
    for (Interceptor interceptor : this.interceptors) {
      retVal =
          interceptor.onFlushDirty(entity, id, currentState, previousState, propertyNames, types);
    }
    return retVal;
  }

  @Override
  public boolean onLoad(
      Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
      throws CallbackException {
    boolean retVal = true;
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.onLoad(entity, id, state, propertyNames, types);
    }
    return retVal;
  }

  // TODO: onPrepareStatement 被移除
  //  @Override
  //  public String onPrepareStatement(String entityName) {
  //    String retVal = "";
  //    for (Interceptor interceptor : this.interceptors) {
  //      retVal = interceptor.onPrepareStatement(entityName);
  //    }
  //    return retVal;
  //  }

  @Override
  public boolean onSave(
      Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types)
      throws CallbackException {
    boolean retVal = true;
    for (Interceptor interceptor : this.interceptors) {
      retVal = interceptor.onSave(entity, id, state, propertyNames, types);
    }
    return retVal;
  }

  @Override
  public void postFlush(Iterator iterator) throws CallbackException {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.postFlush(iterator);
    }
  }

  @Override
  public void preFlush(Iterator iterator) throws CallbackException {
    for (Interceptor interceptor : this.interceptors) {
      interceptor.preFlush(iterator);
    }
  }
}
