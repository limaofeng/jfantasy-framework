package net.asany.jfantasy.framework.dao;

public class DataQueryContextHolder {

  private static final ThreadLocal<DataQueryContext> HOLDER = new ThreadLocal<>();

  public static DataQueryContext getContext() {
    return HOLDER.get();
  }

  public static void setContext(DataQueryContext context) {
    DataQueryContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
    HOLDER.set(context);
  }

  public static void clear() {
    DataQueryContext securityContextHolder = HOLDER.get();
    if (securityContextHolder != null) {
      HOLDER.remove();
    }
  }

  public static DataQueryContext createEmptyContext() {
    return new DataQueryContext();
  }
}
