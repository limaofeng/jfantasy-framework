package org.jfantasy.framework.dao.jpa;

import java.util.Map;

public interface PropertyFilterCustomizer<C> {

  void customize(
      Map<String, TypeConverter<?>> converters, Map<String, PropertyDefinition<?>> properties);
}
