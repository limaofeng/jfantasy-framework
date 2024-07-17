package net.asany.jfantasy.graphql.gateway.execution;

import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.authorization.PolicyBasedAuthorizationProvider;
import net.asany.jfantasy.framework.security.authorization.policy.ResourceAction;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class AuthInstrumentation implements Instrumentation {

  private static final List<String> ROOT_TYPES =
      List.of(new String[] {"Query", "Mutation", "Subscription"});
  private static final List<String> IGNORES =
      List.of(
          new String[] {
            "Query.__schema",
            //    "Query.demoUsers",
            "Mutation.createDemoUser"
          });
  private final PolicyBasedAuthorizationProvider policyBasedAuthorizationProvider;

  public AuthInstrumentation(PolicyBasedAuthorizationProvider policyBasedAuthorizationProvider) {
    this.policyBasedAuthorizationProvider = policyBasedAuthorizationProvider;
  }

  @Override
  public @Nullable InstrumentationContext<Object> beginFieldFetch(
      InstrumentationFieldFetchParameters parameters, InstrumentationState state) {
    DataFetchingEnvironment environment = parameters.getEnvironment();
    String typeName = ((GraphQLObjectType) parameters.getEnvironment().getParentType()).getName();
    String fieldName = parameters.getEnvironment().getField().getName();

    String key = typeName + "." + fieldName;
    log.debug("Fetching field: {}.{}", typeName, fieldName);

    if (IGNORES.contains(key)) {
      return Instrumentation.super.beginFieldFetch(parameters, state);
    }

    Authentication authentication = environment.getGraphQlContext().get("authentication");

    ResourceAction action = policyBasedAuthorizationProvider.getResourceActionForOperation(key);

    Map<String, Object> args = environment.getArguments();
    Set<String> paths = buildResourcePaths(action.getArn(), args);

    if (!ROOT_TYPES.contains(typeName) && "none".equals(action.getId())) {
      return Instrumentation.super.beginFieldFetch(parameters, state);
    }

    if (!policyBasedAuthorizationProvider.authorize(paths, action.getId(), authentication)) {
      throw new AuthenticationGraphQLException(
          environment.getExecutionStepInfo().getPath(),
          "Access denied for field: " + typeName + "." + fieldName);
    }

    return Instrumentation.super.beginFieldFetch(parameters, state);
  }

  private Set<String> buildResourcePaths(Set<String> arns, Map<String, Object> args) {
    return arns.stream().map(item -> replacePlaceholders(item, args)).collect(Collectors.toSet());
  }

  public static String replacePlaceholders(String input, Map<String, Object> dataMap) {
    StringBuilder result = new StringBuilder();
    int length = input.length();
    int currentIndex = 0;

    while (currentIndex < length) {
      int placeholderStart = input.indexOf("{#", currentIndex);
      if (placeholderStart == -1) {
        // 没有找到占位符，将剩余的字符串添加到结果中
        result.append(input.substring(currentIndex));
        break;
      }

      // 将占位符之前的部分添加到结果中
      result.append(input, currentIndex, placeholderStart);

      int placeholderEnd = input.indexOf("}", placeholderStart);
      if (placeholderEnd == -1) {
        // 没有找到匹配的 '}'，将剩余的字符串添加到结果中
        result.append(input.substring(placeholderStart));
        break;
      }

      String placeholder = input.substring(placeholderStart + 2, placeholderEnd);
      String replacement = dataMap.getOrDefault(placeholder, "{#" + placeholder + "}").toString();

      // 将占位符替换为对应的数据
      result.append(replacement);

      currentIndex = placeholderEnd + 1;
    }

    return result.toString();
  }
}
