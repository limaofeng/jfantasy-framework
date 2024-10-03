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
package net.asany.jfantasy.graphql.scalars;

import static net.asany.jfantasy.graphql.util.Kit.typeName;

import graphql.GraphQLContext;
import graphql.execution.CoercedVariables;
import graphql.language.StringValue;
import graphql.language.Value;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Sort;

/**
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
public class OrderCoercing implements Coercing<Sort, String> {

  @Override
  public String serialize(
      @NotNull Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale)
      throws CoercingSerializeException {
    if (input instanceof Sort orderBy) {

      if (orderBy.isUnsorted()) {
        return "unsorted";
      } else {
        StringBuilder sb = new StringBuilder();
        for (Sort.Order order : orderBy) {
          if (!sb.isEmpty()) {
            sb.append(","); // 添加分隔符，如果有多个排序字段
          }
          sb.append(order.getProperty()); // 添加排序字段名称
          sb.append("_");
          sb.append(order.getDirection().toString().toLowerCase()); // 添加排序方向，转换为小写
        }
        return sb.toString();
      }
    }
    return input.toString();
  }

  @Override
  public Sort parseValue(
      Object input, @NotNull GraphQLContext graphQLContext, @NotNull Locale locale)
      throws CoercingParseValueException {
    String inputString = input.toString();
    if ("unsorted".equals(inputString)) {
      return Sort.unsorted();
    }
    if (inputString.contains(",")) {
      return Sort.by(
          Arrays.stream(inputString.split(","))
              .filter(StringUtil::isNotBlank)
              .map(data -> parseValue(data, graphQLContext, locale))
              .reduce(
                  new ArrayList<Sort.Order>(),
                  (all, item) -> {
                    all.addAll(item.toList());
                    return all;
                  },
                  (acc, bcc) -> {
                    acc.addAll(bcc);
                    return acc;
                  })
              .toArray(new Sort.Order[0]));
    }
    if (inputString.contains("(")) {
      // 语法为: createdAt_desc(NATIVE)
      String[] split = inputString.split("\\(");
      String[] sort = split[0].split("_");
      return Sort.by(
          new Sort.Order(
              Sort.Direction.valueOf(sort[1].toUpperCase()),
              sort[0],
              Sort.NullHandling.valueOf(
                  split[1].substring(0, split[1].length() - 1).toUpperCase())));
    } else {
      String[] sort = inputString.split("_");
      return Sort.by(Sort.Direction.valueOf(sort[1].toUpperCase()), sort[0]);
    }
  }

  @Override
  public Sort parseLiteral(
      @NotNull Value<?> input,
      @NotNull CoercedVariables variables,
      @NotNull GraphQLContext graphQLContext,
      @NotNull Locale locale)
      throws CoercingParseLiteralException {
    if (!(input instanceof StringValue)) {
      throw new CoercingParseLiteralException(
          "Expected AST type 'StringValue' but was '" + typeName(input) + "'.");
    }
    return this.parseValue(((StringValue) input).getValue(), graphQLContext, locale);
  }
}
