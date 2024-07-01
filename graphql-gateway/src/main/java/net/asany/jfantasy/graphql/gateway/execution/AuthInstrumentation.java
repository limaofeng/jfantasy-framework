package net.asany.jfantasy.graphql.gateway.execution;

import graphql.ExecutionInput;
import graphql.ExecutionResult;
import graphql.execution.instrumentation.Instrumentation;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.InstrumentationState;
import graphql.execution.instrumentation.parameters.InstrumentationExecutionParameters;
import graphql.execution.instrumentation.parameters.InstrumentationFieldFetchParameters;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.security.authorization.AuthorizationService;
import net.asany.jfantasy.framework.security.authorization.ResourceService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Slf4j
public class AuthInstrumentation implements Instrumentation {

  private final ResourceService resourceService;

  private final AuthorizationService authorizationService;

  public AuthInstrumentation(
      ResourceService resourceService, AuthorizationService authorizationService) {
    this.resourceService = resourceService;
    this.authorizationService = authorizationService;
  }

  @Override
  public @Nullable InstrumentationContext<Object> beginFieldFetch(
      InstrumentationFieldFetchParameters parameters, InstrumentationState state) {
    String typeName = ((GraphQLObjectType) parameters.getEnvironment().getParentType()).getName();
    String fieldName = parameters.getEnvironment().getField().getName();

    log.debug("Fetching field: {}.{}", typeName, fieldName);

    this.resourceService.getResource(typeName + "." + fieldName);

    authorizationService.hasPermission(typeName + "." + fieldName, "", null);

    // 这里可以添加你的权限验证逻辑
    if (!checkUserPermission(parameters.getEnvironment())) {
      throw new RuntimeException("Access denied for field: " + fieldName);
    }
    return Instrumentation.super.beginFieldFetch(parameters, state);
  }

  private boolean checkUserPermission(DataFetchingEnvironment environment) {
    // 根据environment进行权限验证
    // 例如检查用户角色或者特定的权限标记
    return true; // 这里假设权限检查总是通过
  }

  @Override
  public @NotNull ExecutionInput instrumentExecutionInput(
      ExecutionInput executionInput,
      InstrumentationExecutionParameters parameters,
      InstrumentationState state) {
    return Instrumentation.super.instrumentExecutionInput(executionInput, parameters, state);
  }

  @Override
  public @NotNull CompletableFuture<ExecutionResult> instrumentExecutionResult(
      ExecutionResult executionResult,
      InstrumentationExecutionParameters parameters,
      InstrumentationState state) {
    return Instrumentation.super.instrumentExecutionResult(executionResult, parameters, state);
  }
}
