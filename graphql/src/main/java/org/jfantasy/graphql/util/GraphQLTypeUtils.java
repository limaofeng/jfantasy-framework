package org.jfantasy.graphql.util;

import graphql.Scalars;
import graphql.language.*;
import graphql.scalars.ExtendedScalars;
import graphql.schema.*;
import java.lang.reflect.Method;
import java.util.*;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.graphql.gateway.config.GraphQLServiceOverride;
import org.jfantasy.graphql.gateway.service.GraphQLService;

public class GraphQLTypeUtils {

  private static final Method OBJECT_TYPE_BUILD_DEFINITION_MAP =
      ClassUtil.getDeclaredMethod(GraphQLObjectType.class, "buildDefinitionMap");
  private static final Method INPUT_OBJECT_TYPE_BUILD_DEFINITION_MAP =
      ClassUtil.getDeclaredMethod(GraphQLInputObjectType.class, "buildDefinitionMap");
  private static final Map<String, GraphQLScalarType> DEFAULT_SCALARS = new HashMap<>();

  private static final Map<String, Coercing<?, ?>> DEFAULT_COERCINGS = new HashMap<>();

  static {
    DEFAULT_SCALARS.put(Scalars.GraphQLInt.getName(), Scalars.GraphQLInt);
    DEFAULT_SCALARS.put(Scalars.GraphQLFloat.getName(), Scalars.GraphQLFloat);
    DEFAULT_SCALARS.put(Scalars.GraphQLString.getName(), Scalars.GraphQLString);
    DEFAULT_SCALARS.put(Scalars.GraphQLBoolean.getName(), Scalars.GraphQLBoolean);
    DEFAULT_SCALARS.put(Scalars.GraphQLID.getName(), Scalars.GraphQLID);
    // 自定义 Scalar
    DEFAULT_COERCINGS.put("DateTime", ExtendedScalars.DateTime.getCoercing());
    DEFAULT_COERCINGS.put("Date", ExtendedScalars.Date.getCoercing());
    DEFAULT_COERCINGS.put("Json", ExtendedScalars.Json.getCoercing());
    DEFAULT_COERCINGS.put("JSON", ExtendedScalars.Json.getCoercing());
    DEFAULT_COERCINGS.put("Long", ExtendedScalars.GraphQLLong.getCoercing());
    DEFAULT_COERCINGS.put("Hex", ExtendedScalars.GraphQLChar.getCoercing());
    DEFAULT_COERCINGS.put("RGBA", ExtendedScalars.GraphQLChar.getCoercing());
    DEFAULT_COERCINGS.put("RGBAHue", ExtendedScalars.GraphQLChar.getCoercing());
    DEFAULT_COERCINGS.put("RGBATransparency", ExtendedScalars.GraphQLChar.getCoercing());
    DEFAULT_COERCINGS.put("RichTextAST", ExtendedScalars.GraphQLChar.getCoercing());
    DEFAULT_COERCINGS.put("File", Scalars.GraphQLString.getCoercing());
    DEFAULT_COERCINGS.put("OrderBy", Scalars.GraphQLString.getCoercing());
    DEFAULT_COERCINGS.put("Number", ExtendedScalars.GraphQLLong.getCoercing());
    DEFAULT_COERCINGS.put("Upload", Scalars.GraphQLString.getCoercing());
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

  private static GraphQLInputType getGraphQLInputType(Type<?> type, GraphQLService service) {
    if (type instanceof ListType) {
      return GraphQLList.list(getGraphQLInputType(((ListType) type).getType(), service));
    } else if (type instanceof NonNullType) {
      return GraphQLNonNull.nonNull(getGraphQLInputType(((NonNullType) type).getType(), service));
    } else if (type instanceof TypeName) {
      String name = ((TypeName) type).getName();

      if (DEFAULT_SCALARS.containsKey(name)) {
        return DEFAULT_SCALARS.get(name);
      }

      if (service.hasType(name)) {
        return service.getInputType(name);
      }

      TypeDefinition<?> typeDefinition = service.getTypeDefinition(name);

      if (typeDefinition instanceof ScalarTypeDefinition) {
        buildScalarType((ScalarTypeDefinition) typeDefinition, service);
      } else if (typeDefinition instanceof EnumTypeDefinition) {
        buildEnumType((EnumTypeDefinition) typeDefinition, service);
      } else if (typeDefinition instanceof InputObjectTypeDefinition inputObjectTypeDefinition) {
        buildInputObjectType(inputObjectTypeDefinition, service);
      } else {
        throw new RuntimeException("未知类型:" + typeDefinition.toString());
      }
      return service.getInputType(name);
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  public static GraphQLInterfaceType getInterfaceType(Type<?> type, GraphQLService service) {
    if (!(type instanceof TypeName)) {
      throw new RuntimeException("未知类型:" + type.toString());
    }
    return (GraphQLInterfaceType) getOutputType(type, service);
  }

  public static GraphQLOutputType getOutputType(Type<?> type, GraphQLService service) {
    if (type instanceof ListType) {
      return GraphQLList.list(getOutputType(((ListType) type).getType(), service));
    } else if (type instanceof NonNullType) {
      return GraphQLNonNull.nonNull(getOutputType(((NonNullType) type).getType(), service));
    } else if (type instanceof TypeName) {
      String name = ((TypeName) type).getName();

      if (DEFAULT_SCALARS.containsKey(name)) {
        return DEFAULT_SCALARS.get(name);
      }

      if (service.hasType(name)) {
        return service.getOutputType(name);
      }

      TypeDefinition<?> typeDefinition = service.getTypeDefinition(name);

      if (typeDefinition instanceof ObjectTypeDefinition) {
        buildObjectType((ObjectTypeDefinition) typeDefinition, service);
      } else if (typeDefinition instanceof EnumTypeDefinition) {
        buildEnumType((EnumTypeDefinition) typeDefinition, service);
      } else if (typeDefinition instanceof ScalarTypeDefinition) {
        buildScalarType((ScalarTypeDefinition) typeDefinition, service);
      } else if (typeDefinition instanceof UnionTypeDefinition) {
        buildUnionType((UnionTypeDefinition) typeDefinition, service);
      } else if (typeDefinition instanceof InterfaceTypeDefinition) {
        buildInterfaceType((InterfaceTypeDefinition) typeDefinition, service);
      } else {
        throw new RuntimeException("未知类型:" + typeDefinition.toString());
      }
      return service.getOutputType(name);
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  private static void buildInterfaceType(
      InterfaceTypeDefinition typeDefinition, GraphQLService service) {
    GraphQLInterfaceType.Builder typeBuilder =
        GraphQLInterfaceType.newInterface()
            .name(typeDefinition.getName())
            .description(getDescription(typeDefinition.getDescription()))
            .definition(typeDefinition);

    for (FieldDefinition fieldDefinition : typeDefinition.getFieldDefinitions()) {
      GraphQLFieldDefinition.Builder fieldBuilder =
          GraphQLFieldDefinition.newFieldDefinition()
              .name(fieldDefinition.getName())
              .type(getOutputType(fieldDefinition.getType(), service))
              .definition(fieldDefinition);

      typeBuilder.field(fieldBuilder.build());
    }

    service.addType(typeDefinition.getName(), typeBuilder.build());
  }

  private static void buildUnionType(UnionTypeDefinition typeDefinition, GraphQLService service) {
    GraphQLUnionType.Builder typeBuilder =
        GraphQLUnionType.newUnionType()
            .name(typeDefinition.getName())
            .description(getDescription(typeDefinition.getDescription()))
            .definition(typeDefinition);

    for (Type<?> memberType : typeDefinition.getMemberTypes()) {
      typeBuilder.possibleType(GraphQLTypeReference.typeRef(getTypeName(memberType)));
    }

    service.addType(typeDefinition.getName(), typeBuilder.build());
  }

  private static void buildScalarType(ScalarTypeDefinition typeDefinition, GraphQLService service) {
    if (!DEFAULT_COERCINGS.containsKey(typeDefinition.getName())) {
      throw new RuntimeException("未知类型:" + typeDefinition.getName());
    }
    GraphQLScalarType.Builder typeBuilder =
        GraphQLScalarType.newScalar()
            .name(typeDefinition.getName())
            .description(getDescription(typeDefinition.getDescription()))
            .coercing(DEFAULT_COERCINGS.get(typeDefinition.getName()))
            .definition(typeDefinition);

    service.addType(typeDefinition.getName(), typeBuilder.build());
  }

  private static void buildEnumType(EnumTypeDefinition typeDefinition, GraphQLService service) {
    GraphQLEnumType.Builder typeBuilder =
        GraphQLEnumType.newEnum()
            .name(typeDefinition.getName())
            .description(getDescription(typeDefinition.getDescription()));

    for (EnumValueDefinition enumValueDefinition : typeDefinition.getEnumValueDefinitions()) {
      GraphQLEnumValueDefinition.Builder enumValueBuilder =
          GraphQLEnumValueDefinition.newEnumValueDefinition()
              .name(enumValueDefinition.getName())
              .value(enumValueDefinition.getName())
              .description(getDescription(enumValueDefinition.getDescription()))
              .definition(enumValueDefinition);

      typeBuilder.value(enumValueBuilder.build());
    }
    service.addType(typeDefinition.getName(), typeBuilder.build());
  }

  private static String getDescription(Description description) {
    return Optional.ofNullable(description).map(Description::getContent).orElse(null);
  }

  private static void buildInputObjectType(
      InputObjectTypeDefinition typeDefinition, GraphQLService service) {
    GraphQLInputObjectType.Builder typeBuilder =
        GraphQLInputObjectType.newInputObject()
            .name(typeDefinition.getName())
            .description(getDescription(typeDefinition.getDescription()))
            .definition(typeDefinition);

    GraphQLInputObjectType objectType = typeBuilder.build();
    // 注册类型, 防止循环引用
    service.addType(typeDefinition.getName(), objectType);

    // 构建字段
    List<GraphQLInputObjectField> fieldDefinitions = new ArrayList<>();
    for (InputValueDefinition inputValueDefinition : typeDefinition.getInputValueDefinitions()) {
      GraphQLInputObjectField.Builder fieldBuilder =
          GraphQLInputObjectField.newInputObjectField()
              .name(inputValueDefinition.getName())
              .type(getGraphQLInputType(inputValueDefinition.getType(), service))
              .definition(inputValueDefinition);

      fieldDefinitions.add(fieldBuilder.build());
    }

    if (!fieldDefinitions.isEmpty()) {
      Object fieldDefinitionsByName =
          ClassUtil.invoke(INPUT_OBJECT_TYPE_BUILD_DEFINITION_MAP, objectType, fieldDefinitions);
      ClassUtil.setFieldValue(objectType, "fieldMap", fieldDefinitionsByName);
    }
  }

  public static void buildObjectType(ObjectTypeDefinition typeDefinition, GraphQLService service) {
    GraphQLObjectType.Builder typeBuilder =
        GraphQLObjectType.newObject()
            .name(typeDefinition.getName())
            .description(getDescription(typeDefinition.getDescription()))
            .definition(typeDefinition);

    for (Type<?> type : typeDefinition.getImplements()) {
      typeBuilder.withInterface(getInterfaceType(type, service));
    }

    GraphQLObjectType objectType = typeBuilder.build();

    // 注册类型, 防止循环引用
    service.addType(typeDefinition.getName(), objectType);

    GraphQLServiceOverride override = service.getOverrideConfig();

    // 构建字段
    List<GraphQLFieldDefinition> fieldDefinitions = new ArrayList<>();
    for (FieldDefinition fieldDefinition : typeDefinition.getFieldDefinitions()) {

      if (override.shouldIgnoreField(typeDefinition.getName(), fieldDefinition.getName())) {
        continue;
      }

      String fieldName =
          override.getFieldRename(typeDefinition.getName(), fieldDefinition.getName());

      GraphQLFieldDefinition.Builder fieldBuilder =
          GraphQLFieldDefinition.newFieldDefinition()
              .name(fieldName)
              .type(GraphQLTypeUtils.getOutputType(fieldDefinition.getType(), service))
              .definition(fieldDefinition);

      for (InputValueDefinition inputValueDefinition : fieldDefinition.getInputValueDefinitions()) {
        String argumentName =
            override.getFieldArgumentRename(
                typeDefinition.getName(),
                fieldDefinition.getName(),
                inputValueDefinition.getName());

        GraphQLArgument.Builder argumentBuilder =
            GraphQLArgument.newArgument()
                .name(argumentName)
                .type(GraphQLTypeUtils.getGraphQLInputType(inputValueDefinition.getType(), service))
                .definition(inputValueDefinition);
        fieldBuilder.argument(argumentBuilder.build());
      }

      fieldDefinitions.add(fieldBuilder.build());
    }

    if (!fieldDefinitions.isEmpty()) {
      Object fieldDefinitionsByName =
          ClassUtil.invoke(OBJECT_TYPE_BUILD_DEFINITION_MAP, objectType, fieldDefinitions);
      ClassUtil.setFieldValue(objectType, "fieldDefinitionsByName", fieldDefinitionsByName);
    }
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
