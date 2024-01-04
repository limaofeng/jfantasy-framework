package net.asany.jfantasy.graphql.gateway.util;

import graphql.language.VariableReference;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.asany.jfantasy.graphql.gateway.config.FieldResolve;
import org.jetbrains.annotations.NotNull;

public class ResolveExpressionParser {
  private static final Pattern PATTERN = Pattern.compile("([a-zA-Z_][a-zA-Z_0-9]*)\\(([^)]*)\\)");

  public static @NotNull FieldResolve parse(String expression) {
    Matcher matcher = PATTERN.matcher(expression);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid resolve expression: " + expression);
    }

    String functionName = matcher.group(1);
    String paramsSection = matcher.group(2);

    Map<String, FieldResolve.FieldArgument> params = parseParams(paramsSection);
    return FieldResolve.builder().query(functionName).arguments(params).build();
  }

  private static Map<String, FieldResolve.FieldArgument> parseParams(String paramsSection) {
    Map<String, FieldResolve.FieldArgument> params = new HashMap<>();
    String[] pairs = paramsSection.split(",");
    for (String pair : pairs) {
      String[] keyValue = pair.trim().split(":");
      if (keyValue.length != 2) {
        throw new IllegalArgumentException("Invalid param format: " + pair);
      }
      String argName = keyValue[0].trim();
      String argValue = keyValue[1].trim();
      FieldResolve.FieldArgument.Builder argumentBuilder =
          FieldResolve.FieldArgument.builder().name(argName).sourceValue(argValue);

      ;
      if (argValue.startsWith("$")) {
        argumentBuilder.reference(true).value(VariableReference.of(argValue.substring(1)));
      } else {
        argumentBuilder.value(GraphQLValueUtils.parseValue(argValue));
      }

      params.put(argName, argumentBuilder.build());
    }
    return params;
  }
}
