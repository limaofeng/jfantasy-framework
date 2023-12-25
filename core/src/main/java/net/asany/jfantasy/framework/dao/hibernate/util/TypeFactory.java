package net.asany.jfantasy.framework.dao.hibernate.util;

import org.hibernate.type.BasicTypeRegistry;
import org.hibernate.type.Type;
import org.hibernate.type.spi.TypeConfiguration;

public class TypeFactory {

  private TypeFactory() {}

  private static final BasicTypeRegistry typeResolver =
      new TypeConfiguration().getBasicTypeRegistry();

  public static Type basic(String name) {
    return typeResolver.getRegisteredType(name);
  }
}
