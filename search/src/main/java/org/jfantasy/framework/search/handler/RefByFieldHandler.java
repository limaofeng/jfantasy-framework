package org.jfantasy.framework.search.handler;

import org.jfantasy.framework.search.DocumentData;
import org.jfantasy.framework.search.annotations.IndexRefBy;
import org.jfantasy.framework.util.reflect.Property;

import java.util.List;

public class RefByFieldHandler extends ByFieldHandler {
  private Class<?> refBy;

  public RefByFieldHandler(Class<?> refBy, Object obj, Property property, String prefix) {
    super(obj, property, prefix);
    this.refBy = refBy;
  }

  @Override
  public void handle(DocumentData doc) {
    IndexRefBy irb = this.property.getAnnotation(IndexRefBy.class);
    Class<?>[] cls = irb.value();
    int len = cls.length;
    for (int i = 0; i < len; i++) {
      if (cls[i].equals(this.refBy)) {
        boolean analyze = false;
        boolean[] as = irb.index();
        if (as.length > 0) {
          analyze = as[i];
        }
        boolean store = false;
        boolean[] ss = irb.store();
        if (ss.length > 0) {
          store = ss[i];
        }
        double boost = 1.0F;
        double[] bs = irb.boost();
        if (bs.length > 0) {
          boost = bs[i];
        }
        if (this.obj instanceof List<?>) {
          processList((List<?>) this.obj, doc, analyze, store, boost);
          break;
        }
        process(doc, analyze, store, boost);
        break;
      }
    }
  }
}
