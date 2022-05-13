package org.jfantasy.framework.search.backend;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Id;
import org.jfantasy.framework.search.BuguIndex;
import org.jfantasy.framework.search.annotations.IndexRefBy;
import org.jfantasy.framework.search.cache.PropertysCache;
import org.jfantasy.framework.util.reflect.Property;

public class EntityChangedListener {
  private final Class<?> clazz;
  private boolean onlyIdRefBy;
  private RefEntityChangedListener refListener;

  //  private ClusterConfig cluster() {
  //    return BuguIndex.getInstance().getClusterConfig();
  //  }

  public EntityChangedListener(Class clazz) {
    this.clazz = clazz;
    Set<Class<?>> refBySet = new HashSet<>();
    boolean byId = false;
    boolean byOther = false;
    Property[] properties = PropertysCache.getInstance().get(clazz);
    for (Property p : properties) {
      IndexRefBy irb = p.getAnnotation(IndexRefBy.class);
      if (irb != null) {
        Class<?>[] cls = irb.value();
        refBySet.addAll(Arrays.asList(cls));
        if (p.getAnnotation(Id.class) != null) {
          byId = true;
        } else {
          byOther = true;
        }
      }
    }
    if (!refBySet.isEmpty()) {
      this.refListener = new RefEntityChangedListener(refBySet);
      if ((byId) && (!byOther)) {
        this.onlyIdRefBy = true;
      }
    }
  }

  public void entityInsert(Object entity) {
    IndexFilterChecker checker = new IndexFilterChecker(entity);
    if (checker.needIndex()) {
      IndexInsertTask task = new IndexInsertTask(entity);
      BuguIndex.getInstance().getExecutor().execute(task);
    }
  }

  public void entityUpdate(Object entity) {
    String id = PropertysCache.getInstance().getIdProperty(clazz).getValue(entity).toString();
    IndexFilterChecker checker = new IndexFilterChecker(entity);
    if (checker.needIndex()) {
      IndexUpdateTask task = new IndexUpdateTask(entity);
      BuguIndex.getInstance().getExecutor().execute(task);
    } else {
      processRemove(id);
    }
    if ((this.refListener != null) && (!this.onlyIdRefBy)) {
      processRefBy(id);
    }
  }

  public void entityRemove(String id) {
    processRemove(id);
    if (this.refListener != null) {
      processRefBy(id);
    }
  }

  private void processRemove(String id) {
    IndexRemoveTask task = new IndexRemoveTask(this.clazz, id);
    BuguIndex.getInstance().getExecutor().execute(task);
  }

  private void processRefBy(String id) {
    this.refListener.entityChange(this.clazz, id);
  }

  public RefEntityChangedListener getRefListener() {
    return this.refListener;
  }
}
