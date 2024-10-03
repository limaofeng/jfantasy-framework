/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.graphql.gateway.util;

import graphql.Scalars;
import graphql.language.*;
import graphql.parser.Parser;
import graphql.schema.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GraphQLTypeUtils {

  private static final Map<String, GraphQLScalarType> DEFAULT_SCALARS = new HashMap<>();

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

  public static TypeName getTypeName(Type<?> type) {
    if (type instanceof NonNullType nonNullType) {
      return getTypeName(nonNullType.getType());
    } else if (type instanceof TypeName typeName) {
      return typeName;
    } else if (type instanceof ListType listType) {
      return getTypeName(listType.getType());
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  public static String getTypeSource(Type<?> type) {
    if (type instanceof NonNullType nonNullType) {
      return getTypeSource(nonNullType.getType()) + "!";
    } else if (type instanceof TypeName typeName) {
      return typeName.getName();
    } else if (type instanceof ListType listType) {
      return "[" + getTypeSource(listType.getType()) + "]";
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  public static <T extends Type<?>> T parseReturnType(String type) {
    if (type.endsWith("!")) {
      //noinspection unchecked
      return (T)
          NonNullType.newNonNullType(parseReturnType(type.substring(0, type.length() - 1)))
              .build(); // 移除非空标记
    }
    if (type.startsWith("[")) {
      // 处理数组类型
      String elementType = type.substring(1, type.length() - 1);
      //noinspection unchecked
      return (T) ListType.newListType(parseReturnType(elementType)).build();
    } else {
      //noinspection unchecked
      return (T) TypeName.newTypeName(type).build();
    }
  }

  public static Type<?> changeType(Type<?> type, TypeName typeName) {
    if (type instanceof NonNullType) {
      return NonNullType.newNonNullType(changeType(((NonNullType) type).getType(), typeName))
          .build();
    } else if (type instanceof TypeName) {
      return typeName;
    } else if (type instanceof ListType) {
      return ListType.newListType(changeType(((ListType) type).getType(), typeName)).build();
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

  public static boolean isObjectType(GraphQLType fieldType) {
    return getSourceType(fieldType) instanceof GraphQLObjectType;
  }

  public static boolean isFieldsContainerType(GraphQLType fieldType) {
    return getSourceType(fieldType) instanceof GraphQLFieldsContainer;
  }

  public static boolean isListType(Type<?> type) {
    if (type instanceof NonNullType nonNullType) {
      return isListType(nonNullType.getType());
    }
    return type instanceof ListType;
  }

  public static boolean isListType(GraphQLType type) {
    if (type instanceof GraphQLList) {
      return true;
    } else if (type instanceof GraphQLNonNull type1) {
      return isListType(type1.getWrappedType());
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
    if (type instanceof GraphQLObjectType objectType) {
      GraphQLOutputType outputType = objectType.getField(name).getType();
      return GraphQLTypeUtils.getSourceType(outputType);
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  public static GraphQLOutputType toOutputType(Type<?> type) {
    if (type instanceof NonNullType nonNullType) {
      return GraphQLNonNull.nonNull(toOutputType(nonNullType.getType()));
    } else if (type instanceof TypeName typeName) {
      return GraphQLTypeReference.typeRef(typeName.getName());
    } else if (type instanceof ListType listType) {
      return GraphQLList.list(toOutputType(listType.getType()));
    }
    throw new RuntimeException("未知类型:" + type.toString());
  }

  public static DirectiveDefinition parseDirectiveDefinition(String directiveDefinitionString) {
    return Parser.parse(directiveDefinitionString)
        .getFirstDefinitionOfType(DirectiveDefinition.class)
        .orElseThrow(() -> new RuntimeException("无效的指令定义:" + directiveDefinitionString));
  }

  public static GraphQLType convert(Type<?> languageType, GraphQLSchema schema) {
    if (languageType instanceof TypeName typeName) {
      return schema.getType(typeName.getName());
    } else if (languageType instanceof NonNullType nonNullType) {
      GraphQLType innerType = convert(nonNullType.getType(), schema);
      return GraphQLNonNull.nonNull(innerType);
    } else if (languageType instanceof ListType listType) {
      GraphQLType innerType = convert(listType.getType(), schema);
      return GraphQLList.list(innerType);
    }
    throw new UnsupportedOperationException(
        "Conversion for type " + languageType.getClass() + " is not supported.");
  }
}
