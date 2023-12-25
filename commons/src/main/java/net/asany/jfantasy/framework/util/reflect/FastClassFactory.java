package net.asany.jfantasy.framework.util.reflect;

import java.util.HashMap;
import java.util.Map;

public class FastClassFactory implements IClassFactory {
  private final Map<Class<?>, IClass<?>> classes = new HashMap<>();

  @Override
  public <T> IClass<T> getClass(Class<T> cla) {
    if (!this.classes.containsKey(cla)) {
      this.classes.put(cla, new FastClasses<>(cla));
    }
    return (IClass<T>) this.classes.get(cla);
  }
}
