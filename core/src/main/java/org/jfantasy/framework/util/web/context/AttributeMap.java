package org.jfantasy.framework.util.web.context;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AttributeMap implements Map {
  protected static final String UNSUPPORTED = "method makes no sense for a simplified map";
  private static final Object PAGE_CONTEXT = null;
  Map context;

  public AttributeMap(Map context) {
    this.context = context;
  }

  @Override
  public boolean isEmpty() {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public boolean containsKey(Object key) {
    return get(key) != null;
  }

  @Override
  public boolean containsValue(Object value) {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public Set entrySet() {
    return Collections.EMPTY_SET;
  }

  @Override
  public Object get(Object key) {
    Map request = (Map) context.get("request");
    Map session = (Map) context.get("session");
    Map application = (Map) context.get("application");
    if ((request != null) && (request.get(key) != null)) {
      return request.get(key);
    } else if ((session != null) && (session.get(key) != null)) {
      return session.get(key);
    } else if ((application != null) && (application.get(key) != null)) {
      return application.get(key);
    }
    return null;
  }

  @Override
  public Set keySet() {
    return Collections.EMPTY_SET;
  }

  @Override
  public Object put(Object key, Object value) {
    return null;
  }

  @Override
  public void putAll(Map t) {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public Object remove(Object key) {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public int size() {
    throw new UnsupportedOperationException(UNSUPPORTED);
  }

  @Override
  public Collection values() {
    return Collections.EMPTY_SET;
  }
}
