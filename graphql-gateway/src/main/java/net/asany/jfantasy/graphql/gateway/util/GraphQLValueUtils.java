package net.asany.jfantasy.graphql.gateway.util;

import static graphql.Scalars.*;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.GraphQLContext;
import graphql.schema.*;
import java.util.Locale;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.jackson.JSON;
import net.asany.jfantasy.framework.util.common.ObjectUtil;

@Slf4j
public class GraphQLValueUtils {
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

  public static Object convert(
      Object root,
      String name,
      GraphQLOutputType type,
      GraphQLContext graphQLContext,
      Locale locale) {
    GraphQLType outputType = GraphQLTypeUtils.getSourceType(type);

    if (root instanceof JsonNode node) {
      JsonNode valueNode = JSON.findNode(node, "/" + name);
      if (valueNode == null || valueNode.isNull()) {
        return null;
      }
      if (!valueNode.isObject() && GraphQLTypeUtils.isObjectType(outputType)) {
        log.warn(
            "valueNode is not object, but outputType is object. valueNode: {}, outputType: {}",
            valueNode,
            ((GraphQLObjectType) outputType).getName());
        return null;
      }
      if (outputType instanceof GraphQLScalarType scalarType) {
        return GraphQLValueUtils.convertToScalarType(valueNode, scalarType, graphQLContext, locale);
      }
      return valueNode;
    }

    return ObjectUtil.getValue(name, root);
  }

  private static Object convertToScalarType(Object root, GraphQLScalarType scalarType) {
    if (root instanceof JsonNode node) {
      return convertToScalarType(node, scalarType);
    }
    return null;
  }
}
