package net.asany.jfantasy.framework.search.handler;

import java.util.List;
import net.asany.jfantasy.framework.search.Document;
import net.asany.jfantasy.framework.search.annotations.IndexRefBy;
import net.asany.jfantasy.framework.util.reflect.Property;

public class RefByFieldHandler extends ByFieldHandler {
  private Class<?> refBy;

  public RefByFieldHandler(Class<?> refBy, Object obj, Property property, String prefix) {
    super(obj, property, prefix);
    this.refBy = refBy;
  }

  public RefByFieldHandler(Property property, String prefix) {
    super(property, prefix);
  }

  @Override
  public void handle(Document doc) {
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
        process(doc);
        break;
      }
    }
  }
}
