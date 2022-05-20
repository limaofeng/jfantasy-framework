package org.jfantasy.framework.search.elastic;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.AnnotatedParameter;
import org.jfantasy.framework.search.annotations.IndexProperty;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.reflect.Property;

public class ElasticPropertyNamingStrategy extends PropertyNamingStrategy {
  @Override
  public String nameForField(MapperConfig<?> config, AnnotatedField field, String defaultName) {
    return super.nameForField(config, field, defaultName);
  }

  @Override
  public String nameForGetterMethod(
      MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    Property property = ClassUtil.getProperty(method.getDeclaringClass(), defaultName);
    IndexProperty indexProperty = property.getAnnotation(IndexProperty.class);
    if (indexProperty == null || StringUtil.isBlank(indexProperty.name())) {
      return super.nameForSetterMethod(config, method, defaultName);
    }
    return indexProperty.name();
  }

  @Override
  public String nameForSetterMethod(
      MapperConfig<?> config, AnnotatedMethod method, String defaultName) {
    Property property = ClassUtil.getProperty(method.getDeclaringClass(), defaultName);
    IndexProperty indexProperty = property.getAnnotation(IndexProperty.class);
    if (indexProperty == null || StringUtil.isBlank(indexProperty.name())) {
      return super.nameForSetterMethod(config, method, defaultName);
    }
    return indexProperty.name();
  }

  @Override
  public String nameForConstructorParameter(
      MapperConfig<?> config, AnnotatedParameter ctorParam, String defaultName) {
    return super.nameForConstructorParameter(config, ctorParam, defaultName);
  }
}
