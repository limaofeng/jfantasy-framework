package org.jfantasy.framework.search.backend;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;
import org.jfantasy.framework.search.CuckooIndex;
import org.jfantasy.framework.search.annotations.IndexRef;
import org.jfantasy.framework.search.annotations.IndexRefList;
import org.jfantasy.framework.search.cache.DaoCache;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.search.dao.DataFetcher;
import org.jfantasy.framework.util.reflect.Property;

public class RefEntityChangedListener {
  private final Set<Class<?>> refBySet;

  public RefEntityChangedListener(Set<Class<?>> refBySet) {
    this.refBySet = refBySet;
  }

  public void entityChange(Class<?> refClass, String id) {
    for (Class<?> cls : this.refBySet) {
      for (Property p : PropertysCache.getInstance().get(cls)) {
        processField(refClass, id, cls, p);
      }
    }
  }

  private void processField(Class<?> refClass, String id, Class<?> cls, Property p) {
    boolean match = false;
    String fieldName = p.getName();
    if (p.getPropertyType().equals(refClass) && (p.getAnnotation(IndexRef.class) != null)) {
      match = true;
    } else {
      if (p.getAnnotation(IndexRefList.class) != null) {
        Class<?> c;
        Class<?> type = p.getPropertyType();
        if (type.isArray()) {
          c = type.getComponentType();
        } else {
          ParameterizedType paramType = p.getGenericType();
          Type[] types = paramType.getActualTypeArguments();
          if (types.length == 1) {
            c = (Class<?>) types[0];
          } else {
            c = (Class<?>) types[1];
          }
        }
        if ((c != null) && (c.equals(refClass))) {
          match = true;
        }
      }
    }
    if (match) {
      //      Session session = OpenSessionUtils.openSession();
      try {
        DataFetcher dao = DaoCache.getInstance().get(cls);
        if (dao != null) {
          List<?> list = dao.findByField(fieldName, id);
          for (Object o : list) {
            IndexUpdateTask task = new IndexUpdateTask(o);
            CuckooIndex.getInstance().getExecutor().execute(task);
          }
        }
      } finally {
        //        OpenSessionUtils.closeSession(session);
      }
    }
  }
}
