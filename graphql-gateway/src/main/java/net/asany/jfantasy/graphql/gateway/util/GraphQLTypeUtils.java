package net.asany.jfantasy.graphql.gateway.util;

import graphql.Scalars;
import graphql.language.*;
import graphql.schema.*;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.asany.jfantasy.framework.util.common.ClassUtil;

public class GraphQLTypeUtils {

  private static final Method OBJECT_TYPE_BUILD_DEFINITION_MAP =
      ClassUtil.getDeclaredMethod(GraphQLObjectType.class, "buildDefinitionMap");
  private static final Method INPUT_OBJECT_TYPE_BUILD_DEFINITION_MAP =
      ClassUtil.getDeclaredMethod(GraphQLInputObjectType.class, "buildDefinitionMap");
  private static final Map<String, GraphQLScalarType> DEFAULT_SCALARS = new HashMap<>();

  //  private static final Map<String, Coercing<?, ?>> DEFAULT_COERCINGS = new HashMap<>();

  static {
    DEFAULT_SCALARS.put(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt);
    DEFAULT_SCALARS.put(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat);
    DEFAULT_SCALARS.put(Scalars.GraphQLString.getName(), Scalars.GraphQLString);
    DEFAULT_SCALARS.put(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean);
    DEFAULT_SCALARS.put(Scalars.GraphQLID.getName(), Scalars.GraphQLID);
  }

  public static boolean hasBaseScalar(String name) {
    return DEFAULT_SCALARS.containsKey(name);
  }

  public static String getTypeName(Type<?> type) {
    if (type instanceof NonNullType) {
      return getTypeName(((NonNullType) type).getType());
    } else if (type instanceof TypeName) {
      return ((TypeName) type).getName();
    } else if (type instanceof ListType) {
      return getTypeName(((ListType) type).getType());
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  private static String getDescription(Description description) {
    return Optional.ofNullable(description).map(Description::getContent).orElse(null);
  }

  public static boolean isScalar(GraphQLType type) {
    if (type instanceof GraphQLScalarType) {
      return true;
    } else if (type instanceof GraphQLNonNull type1) {
      return isScalar(type1.getWrappedType());
    } else if (type instanceof GraphQLList type1) {
      return isScalar(type1.getWrappedType());
    }
    return false;
  }

  public static boolean isList(GraphQLType type) {
    if (type instanceof GraphQLList) {
      return true;
    } else if (type instanceof GraphQLNonNull type1) {
      return isList(type1.getWrappedType());
    }
    return false;
  }

  public static GraphQLType getSourceType(GraphQLType type) {
    if (type instanceof GraphQLNonNull wType) {
      return getSourceType(wType.getWrappedType());
    } else if (type instanceof GraphQLList wType) {
      return getSourceType(wType.getWrappedType());
    }
    return type;
  }

  public static GraphQLType getFieldType(GraphQLType type, String name) {
    GraphQLObjectType objectType = (GraphQLObjectType) type;
    GraphQLOutputType outputType = objectType.getField(name).getType();
    return GraphQLTypeUtils.getSourceType(outputType);
  }
}
