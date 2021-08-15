package org.jfantasy.framework.lucene.backend;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.lucene.annotations.*;
import org.jfantasy.framework.lucene.cache.PropertysCache;
import org.jfantasy.framework.lucene.exception.PropertyException;
import org.jfantasy.framework.util.reflect.Property;

public class IndexChecker {
  private IndexChecker() {}

  private static final Logger LOGGER = LogManager.getLogger(IndexChecker.class);

  /**
   * 判断Class是否标注了 @Indexed 注解
   *
   * @param clazz
   * @return
   */
  public static boolean hasIndexed(Class<?> clazz) {
    return clazz.getAnnotation(Indexed.class) != null;
  }

  public static boolean hasIndexAnnotation(Class<?> clazz, String key) {
    boolean result = false;
    int index = key.indexOf(".");
    if (index != -1) {
      key = key.substring(0, index);
    }
    Property property = null;
    try {
      property = PropertysCache.getInstance().getProperty(clazz, key);
    } catch (PropertyException ex) {
      LOGGER.error(ex.getMessage(), ex);
      return false;
    }
    if ((property.getAnnotation(IndexProperty.class) != null)
        || (property.getAnnotation(IndexEmbed.class) != null)
        || (property.getAnnotation(IndexEmbedList.class) != null)
        || (property.getAnnotation(IndexRef.class) != null)
        || (property.getAnnotation(IndexRefList.class) != null)
        || (property.getAnnotation(IndexRefBy.class) != null)
        || (property.getAnnotation(BoostSwitch.class) != null)
        || (property.getAnnotation(IndexFilter.class) != null)) {
      result = true;
    }
    return result;
  }

  /**
   * 判断Class是否标注@indexed注解或者属性是否标注了@IndexRefBy注解
   *
   * @param clazz
   * @return
   */
  public static boolean needListener(Class<?> clazz) {
    boolean result = false;
    if (clazz.getAnnotation(Indexed.class) != null) {
      result = true;
    } else {
      Property[] properties = PropertysCache.getInstance().get(clazz);
      for (Property p : properties) {
        IndexRefBy irb = p.getAnnotation(IndexRefBy.class);
        if (irb != null) {
          result = true;
          break;
        }
      }
    }
    return result;
  }
}
