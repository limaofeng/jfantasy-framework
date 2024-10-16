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

import static graphql.Scalars.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import graphql.GraphQLContext;
import graphql.execution.directives.QueryAppliedDirective;
import graphql.language.*;
import graphql.parser.Parser;
import graphql.schema.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.graphql.gateway.error.GraphQLGatewayException;

@Slf4j
public class GraphQLValueUtils {

  public static Value<?> parseValue(String input) {
    return Parser.parseValue(input);
  }

  //  public static Value<?> convertToValue(
  //      Object source, Type<?> type, TypeDefinition<?> typeDefinition) {
  //    if (source == null) {
  //      return null;
  //    }
  //    if (source instanceof Value<?>) {
  //      return (Value<?>) source;
  //    }
  //    if (type instanceof NonNullType nonNullType) {
  //      return convertToValue(source, nonNullType.getType(), typeDefinition);
  //    } else if (type instanceof ListType listType) {
  //      return convertToValue(source, listType.getType(), typeDefinition);
  //    } else if (type instanceof TypeName typeName) {
  //      if (typeDefinition instanceof ScalarTypeDefinition scalarTypeDefinition) {
  //        return convertToScalarType(source, scalarTypeDefinition);
  //      } else if (typeDefinition instanceof EnumTypeDefinition enumTypeDefinition) {
  //        return EnumValue.of(source.toString());
  //      } else if (typeDefinition instanceof InputObjectTypeDefinition inputObjectTypeDefinition)
  // {
  //        return ObjectValue.newObjectValue().build();
  //      } else if (typeDefinition instanceof ObjectTypeDefinition objectTypeDefinition) {
  //        return ObjectValue.newObjectValue().build();
  //      }
  //    }
  //    throw new IllegalArgumentException("Unsupported type: " + type);
  //  }

  public static Value<?> convertToScalarType(Object value, ScalarTypeDefinition scalarType) {
    if (scalarType.getName().equals(GraphQLString.getName())) {
      return StringValue.of(value.toString());
    } else if (scalarType.getName().equals(GraphQLInt.getName())) {
      return IntValue.of(
          value instanceof Number
              ? ((Number) value).intValue()
              : Integer.parseInt(value.toString()));
    } else if (scalarType.getName().equals(GraphQLBoolean.getName())) {
      return BooleanValue.of(Boolean.parseBoolean(value.toString()));
    } else if (scalarType.getName().equals(GraphQLFloat.getName())) {
      return FloatValue.of(
          value instanceof Number
              ? ((Number) value).doubleValue()
              : Double.parseDouble(value.toString()));
    } else if (scalarType.getName().equals(GraphQLID.getName())) {
      return StringValue.of(value.toString());
    }
    return null;
  }

  /**
   * 根据 GraphQLScalarType 转换 JsonNode 到相应的 Java 对象。
   *
   * @param node JsonNode 对象。
   * @param scalarType GraphQLScalarType 类型。
   * @return 转换后的 Java 对象。
   */
  public static Object convertToScalarType(
      JsonNode node, GraphQLScalarType scalarType, GraphQLContext graphQLContext, Locale locale) {
    if (node == null || node.isNull()) {
      return null;
    }
    if (scalarType == GraphQLString) {
      return node.isTextual() ? node.asText() : null;
    } else if (scalarType == GraphQLInt) {
      return node.isInt() ? node.asInt() : null;
    } else if (scalarType == GraphQLBoolean) {
      return node.isBoolean() ? node.asBoolean() : null;
    } else if (scalarType == GraphQLFloat) {
      return node.isDouble() ? node.asDouble() : null;
    } else if (scalarType == GraphQLID) {
      if (node.isNumber()) {
        return node.asLong();
      }
      return node.isTextual() ? node.asText() : null;
    }
    Object nodeValue = getNodeValue(node);
    Coercing<?, ?> coercing = scalarType.getCoercing();
    assert nodeValue != null;
    return coercing.serialize(nodeValue, graphQLContext, locale);
  }

  @SneakyThrows
  public static Object getNodeValue(JsonNode node) {
    if (node.isTextual()) {
      return node.asText();
    } else if (node.isInt()) {
      return node.asInt();
    } else if (node.isBoolean()) {
      return node.asBoolean();
    } else if (node.isDouble()) {
      return node.asDouble();
    } else if (node.isLong()) {
      return node.asLong();
    } else if (node.isBigDecimal()) {
      return node.decimalValue();
    } else if (node.isBigInteger()) {
      return node.bigIntegerValue();
    } else if (node.isBinary()) {
      return node.binaryValue();
    } else if (node.isFloat()) {
      return node.floatValue();
    } else if (node.isShort()) {
      return node.shortValue();
    } else if (node.isNumber()) {
      return node.numberValue();
    } else if (node.isObject()) {
      return node;
    } else if (node.isArray()) {
      return node;
    }
    return null;
  }

  private static Object processJsonData(
      JsonNode valueNode,
      GraphQLOutputType outputType,
      GraphQLType type,
      GraphQLContext graphQLContext,
      Locale locale) {
    if (valueNode == null || valueNode.isNull()) {
      return null;
    }
    if (GraphQLTypeUtils.isListType(outputType) && valueNode.isArray()) {
      List<Object> list = new ArrayList<>();
      GraphQLType gType = GraphQLTypeUtils.getSourceType(outputType);
      for (JsonNode item : valueNode) {
        if (gType instanceof GraphQLScalarType scalarType) {
          if (scalarType.getName().equals(GraphQLString.getName())) {
            list.add(item.asText());
          } else {
            list.add(item);
          }
        } else {
          list.add(item);
        }
      }
      return list;
    }
    if (!valueNode.isObject() && GraphQLTypeUtils.isObjectType(outputType)) {
      log.warn(
          "valueNode is not object, but outputType is object. valueNode: {}, outputType: {}",
          valueNode,
          ((GraphQLObjectType) type).getName());
      return null;
    }
    if (type instanceof GraphQLScalarType scalarType) {
      return GraphQLValueUtils.convertToScalarType(valueNode, scalarType, graphQLContext, locale);
    }
    if (type instanceof GraphQLEnumType enumType) {
      if (valueNode instanceof TextNode textNode) {
        return textNode.asText();
      }
      throw new GraphQLGatewayException("Invalid enum value: " + enumType);
    }
    return valueNode;
  }

  public static Object convert(
      Object root,
      String name,
      GraphQLOutputType outputType,
      GraphQLContext graphQLContext,
      Locale locale) {
    GraphQLType type = GraphQLTypeUtils.getSourceType(outputType);

    if (root instanceof JsonNode node) {
      JsonNode valueNode = JSON.findNode(node, "/" + name);
      return processJsonData(valueNode, outputType, type, graphQLContext, locale);
    }

    return ObjectUtil.getValue(name, root);
  }

  private static Object convertToScalarType(Object root, GraphQLScalarType scalarType) {
    if (root instanceof JsonNode node) {
      return convertToScalarType(node, scalarType);
    }
    return null;
  }

  public static QueryAppliedDirective getDirective(
      DataFetchingEnvironment environment, String directiveName) {
    List<QueryAppliedDirective> directives =
        environment.getQueryDirectives().getImmediateAppliedDirective(directiveName);
    if (directives.isEmpty()) {
      throw new IllegalArgumentException("Unknown directive: " + directiveName);
    }
    return directives.get(0);
  }

  public static String convertToString(Value<?> value) {
    if (value instanceof NullValue) {
      return null;
    } else if (value instanceof StringValue stringValue) {
      return "\"" + stringValue.getValue() + "\"";
    } else if (value instanceof IntValue intValue) {
      return intValue.getValue().toString();
    } else if (value instanceof FloatValue floatValue) {
      return floatValue.getValue().toString();
    } else if (value instanceof BooleanValue booleanValue) {
      return booleanValue.isValue() ? "true" : "false";
    } else if (value instanceof EnumValue enumValue) {
      return enumValue.getName();
    } else if (value instanceof ObjectValue objectValue) {
      return "{"
          + objectValue.getObjectFields().stream()
              .map(field -> field.getName() + ": " + convertToString(field.getValue()))
              .collect(Collectors.joining(", "))
          + "}";
    } else if (value instanceof ArrayValue arrayValue) {
      StringBuilder builder = new StringBuilder("[");
      for (Value<?> item : arrayValue.getValues()) {
        builder.append(convertToString(item)).append(", ");
      }
      builder.append("]");
      return builder.toString();
    }
    throw new IllegalArgumentException("Unsupported value: " + value);
  }
}
