package org.jfantasy.graphql.execution;

import graphql.ExecutionResult;
import graphql.execution.instrumentation.InstrumentationContext;
import graphql.execution.instrumentation.SimpleInstrumentation;
import graphql.execution.instrumentation.SimpleInstrumentationContext;
import graphql.execution.instrumentation.parameters.InstrumentationExecuteOperationParameters;
import graphql.language.OperationDefinition;
import org.jetbrains.annotations.NotNull;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 用于解决 MUTATION 下的事务问题
 *
 * @author limaofeng
 */
public class GraphQLTransactionInstrumentation extends SimpleInstrumentation {
  private final PlatformTransactionManager transactionManager;

  public GraphQLTransactionInstrumentation(PlatformTransactionManager transactionManager) {
    this.transactionManager = transactionManager;
  }

  @Override
  public @NotNull InstrumentationContext<ExecutionResult> beginExecuteOperation(
      InstrumentationExecuteOperationParameters parameters) {
    TransactionTemplate tx = new TransactionTemplate(this.transactionManager);
    OperationDefinition.Operation operation =
        parameters.getExecutionContext().getOperationDefinition().getOperation();
    if (!OperationDefinition.Operation.MUTATION.equals(operation)) {
      tx.setReadOnly(true);
    }
    TransactionStatus status = this.transactionManager.getTransaction(tx);
    return SimpleInstrumentationContext.whenCompleted(
        (t, e) -> {
          if (!t.getErrors().isEmpty() || e != null) {
            this.transactionManager.rollback(status);
          } else {
            this.transactionManager.commit(status);
          }
        });
  }
}
