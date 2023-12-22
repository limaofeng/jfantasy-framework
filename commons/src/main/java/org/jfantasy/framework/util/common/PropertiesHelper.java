package org.jfantasy.framework.util.common;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.util.SystemPropertyUtils;

/**
 * Properties 的操作的工具类 <br>
 * 为Properties提供一个代理增加相关工具方法如 <br>
 * getRequiredString(),getInt(),getBoolean() 等方法
 */
@Slf4j
public class PropertiesHelper {
  private static final PropertiesHelper NULL_PROPERTIES_HELPER =
      new PropertiesHelper(new Properties());

  private final List<Properties> propertiesList = new ArrayList<>();
  private final ConcurrentMap<String, Properties> propertiesCache = new ConcurrentHashMap<>();

  public static PropertiesHelper load(String propertiesPath) {
    try {
      Iterator<URL> urls = getResources(propertiesPath, PropertiesHelper.class, true);
      PropertiesHelper helper = new PropertiesHelper();
      while (urls.hasNext()) {
        URL url = urls.next();
        helper.add(PropertiesLoaderUtils.loadProperties(new UrlResource(url)));
      }
      return helper;
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      return NULL_PROPERTIES_HELPER;
    }
  }

  public static PropertiesHelper load(Properties props) {
    return new PropertiesHelper(props);
  }

  private void add(Properties properties) {
    this.propertiesList.add(properties);
  }

  public PropertiesHelper(Properties... ps) {
    for (Properties p : ps) {
      this.add(p);
    }
  }

  public PropertiesHelper filter(CallBackMatch callBack) {
    Properties props = new Properties();
    for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
      if (callBack.call(entry)) {
        props.put(entry.getKey(), entry.getValue());
      }
    }
    this.clear();
    this.add(props);
    return this;
  }

  public PropertiesHelper map(CallBackTransform callBack) {
    Properties props = new Properties();
    for (Map.Entry<Object, Object> entry : getProperties().entrySet()) {
      Map.Entry<Object, Object> newEntry = callBack.call(entry);
      props.put(newEntry.getKey(), newEntry.getValue());
    }
    this.clear();
    this.add(props);
    return this;
  }

  public Properties getProperties(String... ignorePropertyNames) {
    Properties props = new Properties();
    String cacheKey = Arrays.toString(ignorePropertyNames);
    if (propertiesCache.containsKey(cacheKey)) {
      return propertiesCache.get(cacheKey);
    }
    for (Properties eProps : propertiesList) {
      for (Map.Entry<Object, Object> entry : eProps.entrySet()) {
        if (ObjectUtil.exists(ignorePropertyNames, entry.getKey())) {
          continue;
        }
        if (!props.containsKey(entry.getKey())) {
          props.put(entry.getKey(), entry.getValue());
        }
      }
    }
    propertiesCache.put(cacheKey, props);
    return props;
  }

  public String getRequiredString(String key) {
    String value = getProperty(key);
    if (StringUtil.isBlank(value)) {
      throw new IllegalStateException("required property is blank by key=" + key);
    }
    return value;
  }

  public String getNullIfBlank(String key) {
    String value = getProperty(key);
    if (StringUtil.isBlank(value)) {
      return null;
    }
    return value;
  }

  public String getNullIfEmpty(String key) {
    String value = getProperty(key);
    if (value == null || "".equals(value)) {
      return null;
    }
    return value;
  }

  public Integer getInteger(String key) {
    String v = getProperty(key);
    if (v == null) {
      return null;
    }
    return Integer.parseInt(v);
  }

  public int getInt(String key, int defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Integer.parseInt(getRequiredString(key));
  }

  public int getRequiredInt(String key) {
    return Integer.parseInt(getRequiredString(key));
  }

  public Long getLong(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Long.parseLong(getRequiredString(key));
  }

  public long getLong(String key, long defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Long.parseLong(getRequiredString(key));
  }

  public Long getRequiredLong(String key) {
    return Long.parseLong(getRequiredString(key));
  }

  public Boolean getBoolean(String key) {
    return getProperty(key) != null && Boolean.parseBoolean(getRequiredString(key));
  }

  public boolean getBoolean(String key, boolean defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Boolean.parseBoolean(getRequiredString(key));
  }

  public boolean getRequiredBoolean(String key) {
    return Boolean.parseBoolean(getRequiredString(key));
  }

  public Float getFloat(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Float.parseFloat(getRequiredString(key));
  }

  public float getFloat(String key, float defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Float.parseFloat(getRequiredString(key));
  }

  public Float getRequiredFloat(String key) {
    return Float.parseFloat(getRequiredString(key));
  }

  public Double getDouble(String key) {
    if (getProperty(key) == null) {
      return null;
    }
    return Double.parseDouble(getRequiredString(key));
  }

  public double getDouble(String key, double defaultValue) {
    if (getProperty(key) == null) {
      return defaultValue;
    }
    return Double.parseDouble(getRequiredString(key));
  }

  public Double getRequiredDouble(String key) {
    return Double.parseDouble(getRequiredString(key));
  }

  public Object setProperty(String key, Object value) {
    return setProperty(key, String.valueOf(value));
  }

  public String getProperty(String key, String defaultValue) {
    String value = getProperties().getProperty(key, defaultValue);
    return StringUtil.isNotBlank(value)
        ? SystemPropertyUtils.resolvePlaceholders(value, true)
        : value;
  }

  public String getProperty(String key) {
    String value = getProperties().getProperty(key);
    return StringUtil.isNotBlank(value)
        ? SystemPropertyUtils.resolvePlaceholders(value, true)
        : value;
  }

  public String[] getMergeProperty(String key) {
    List<String> values = new ArrayList<>();
    for (Properties eProps : propertiesList) {
      for (Object pkey : eProps.keySet()) {
        if (key.equals(pkey)) {
          String value = eProps.getProperty(key);
          if (StringUtil.isNotBlank(value)) {
            values.add(eProps.getProperty(key));
          }
        }
      }
    }
    return values.toArray(new String[0]);
  }

  public Object setProperty(String key, String value) {
    return getProperties().setProperty(key, value);
  }

  public void clear() {
    this.propertiesList.clear();
    this.propertiesCache.clear();
  }

  public int size() {
    return getProperties().size();
  }

  @Override
  public String toString() {
    return getProperties().toString();
  }

  public interface CallBackTransform {

    Map.Entry<Object, Object> call(Map.Entry<Object, Object> entry);
  }

  public interface CallBackMatch {

    boolean call(Map.Entry<Object, Object> entry);
  }

  public static class MapEntry implements Map.Entry<Object, Object> {

    private final Object key;
    private Object value;

    public MapEntry(Object key, Object value) {
      this.key = key;
      this.value = value;
    }

    @Override
    public Object getKey() {
      return this.key;
    }

    @Override
    public Object getValue() {
      return this.value;
    }

    @Override
    public Object setValue(Object value) {
      return this.value = value;
    }
  }

  public static Iterator<URL> getResources(
      String resourceName, Class<?> callingClass, boolean aggregate) throws IOException {

    AggregateIterator<URL> iterator = new AggregateIterator<>();

    iterator.addEnumeration(
        Thread.currentThread().getContextClassLoader().getResources(resourceName));

    if (!iterator.hasNext() || aggregate) {
      iterator.addEnumeration(PropertiesHelper.class.getClassLoader().getResources(resourceName));
    }

    if (!iterator.hasNext() || aggregate) {
      ClassLoader cl = callingClass.getClassLoader();

      if (cl != null) {
        iterator.addEnumeration(cl.getResources(resourceName));
      }
    }

    if (!iterator.hasNext()
        && (resourceName != null)
        && ((resourceName.isEmpty()) || (resourceName.charAt(0) != '/'))) {
      return getResources('/' + resourceName, callingClass, aggregate);
    }

    return iterator;
  }

  static class AggregateIterator<E> implements Iterator<E> {

    LinkedList<Enumeration<E>> enums = new LinkedList<>();
    Enumeration<E> cur = null;
    E next = null;
    Set<E> loaded = new HashSet<>();

    public AggregateIterator<E> addEnumeration(Enumeration<E> e) {
      if (e.hasMoreElements()) {
        if (cur == null) {
          cur = e;
          next = e.nextElement();
          loaded.add(next);
        } else {
          enums.add(e);
        }
      }
      return this;
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public E next() {
      if (next != null) {
        E prev = next;
        next = loadNext();
        return prev;
      } else {
        throw new NoSuchElementException();
      }
    }

    private Enumeration<E> determineCurrentEnumeration() {
      if (cur != null && !cur.hasMoreElements()) {
        if (!enums.isEmpty()) {
          cur = enums.removeLast();
        } else {
          cur = null;
        }
      }
      return cur;
    }

    private E loadNext() {
      if (determineCurrentEnumeration() != null) {
        E tmp = cur.nextElement();
        int loadedSize = loaded.size();
        while (loaded.contains(tmp)) {
          tmp = loadNext();
          if (tmp == null || loaded.size() > loadedSize) {
            break;
          }
        }
        if (tmp != null) {
          loaded.add(tmp);
        }
        return tmp;
      }
      return null;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
