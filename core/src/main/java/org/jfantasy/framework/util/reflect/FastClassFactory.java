package org.jfantasy.framework.util.reflect;

import java.util.HashMap;
import java.util.Map;

public class FastClassFactory implements IClassFactory {
  private Map<String, IClass<?>> classes = new HashMap<>();

  @Override
  @SuppressWarnings({"unchecked", "rawtypes"})
  public <T> IClass<T> getClass(Class<T> cla) {
    if (!this.classes.containsKey(cla.getName())) {
      this.classes.put(cla.getName(), new FastClasses(cla));
    }
    return (IClass<T>) this.classes.get(cla.getName());
  }
}
