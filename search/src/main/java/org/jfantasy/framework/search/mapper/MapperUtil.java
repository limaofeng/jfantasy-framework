package org.jfantasy.framework.search.mapper;

public class MapperUtil {

  private MapperUtil() {}

  public static String getEntityName(Class<?> clazz) {
    return clazz.getName().toLowerCase();
  }
}
