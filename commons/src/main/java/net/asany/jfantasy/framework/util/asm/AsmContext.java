package net.asany.jfantasy.framework.util.asm;

import java.util.HashMap;
import java.util.Map;

public class AsmContext {

  private static final ThreadLocal<AsmContext> threadLocal = new ThreadLocal<>();
  private final Map<String, Object> data = new HashMap<>();

  public static AsmContext getContext() {
    AsmContext context = threadLocal.get();
    if (context == null) {
      threadLocal.set(context = new AsmContext());
    }
    return context;
  }

  public Object get(String key) {
    return data.get(key);
  }

  public void set(String key, Object value) {
    data.put(key, value);
  }

  public <T> T get(String key, Class<T> classed) {
    return classed.cast(get(key));
  }
}
