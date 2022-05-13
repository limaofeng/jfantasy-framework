package org.jfantasy.framework.util.web.context;

import java.util.*;
import javax.servlet.http.HttpServletRequest;
import org.jfantasy.framework.util.common.StringUtil;

public class ParameterMap extends HashMap<String, String[]> {

  private static final long serialVersionUID = -4464409070399094933L;

  private transient HttpServletRequest request;

  public ParameterMap(final HttpServletRequest request) {
    this.request = request;
  }

  private transient Set<Map.Entry<String, String[]>> entries;

  @Override
  public String[] get(Object key) {
    return request.getParameterValues(StringUtil.nullValue(key));
  }

  @SuppressWarnings("unchecked")
  @Override
  public Set<Map.Entry<String, String[]>> entrySet() {
    if (entries == null) {
      entries = new HashSet<Map.Entry<String, String[]>>();
      Enumeration<String> enumeration = request.getParameterNames();
      while (enumeration.hasMoreElements()) {
        final String key = enumeration.nextElement();
        final String[] value = request.getParameterValues(key);
        entries.add(
            new Entry<String, String[]>() {

              @Override
              @SuppressWarnings("rawtypes")
              public boolean equals(Object obj) {
                if (!(obj instanceof Entry)) {
                  return false;
                }
                Entry<String, String[]> entry = (Entry<String, String[]>) obj;
                return key == null
                    ? (entry.getKey() == null)
                    : key.equals(entry.getKey()) && value == null
                        ? (entry.getValue() == null)
                        : value == entry.getValue();
              }

              @Override
              public int hashCode() {
                return ((key == null) ? 0 : key.hashCode())
                    ^ ((value == null) ? 0 : Arrays.hashCode(value));
              }

              @Override
              public String getKey() {
                return key;
              }

              @Override
              public String[] getValue() {
                return value;
              }

              @Override
              public String[] setValue(String[] value) {
                return value;
              }
            });
      }
    }
    return entries;
  }
}
