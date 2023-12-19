package org.jfantasy.graphql.util;

import static graphql.Scalars.*;

import com.fasterxml.jackson.databind.JsonNode;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLType;
import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.util.common.ObjectUtil;

public class GraphQLValueUtils {
  /**
   * 根据 GraphQLScalarType 转换 JsonNode 到相应的 Java 对象。
   *
   * @param node JsonNode 对象。
   * @param scalarType GraphQLScalarType 类型。
   * @return 转换后的 Java 对象。
   */
  public static Object convertToScalarType(JsonNode node, GraphQLScalarType scalarType) {
    if (node == null) {
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
    throw new UnsupportedOperationException("不支持的类型:" + scalarType.getName());
  }

  public static Object convert(Object root, String name, GraphQLOutputType type) {
    GraphQLType outputType = GraphQLTypeUtils.getSourceType(type);

    if (root instanceof JsonNode node) {
      JsonNode valueNode = JSON.findNode(node, "/" + name);
      if (outputType instanceof GraphQLScalarType scalarType) {
        return GraphQLValueUtils.convertToScalarType(valueNode, scalarType);
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
