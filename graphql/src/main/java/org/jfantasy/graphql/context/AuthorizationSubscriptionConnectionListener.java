package org.jfantasy.graphql.context;

import graphql.kickstart.execution.subscriptions.SubscriptionSession;
import graphql.kickstart.execution.subscriptions.apollo.ApolloSubscriptionConnectionListener;
import graphql.kickstart.execution.subscriptions.apollo.OperationMessage;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * 订阅连接监听器
 *
 * @author limaofeng
 */
@Component
public class AuthorizationSubscriptionConnectionListener
    implements ApolloSubscriptionConnectionListener {

  @Override
  public void onConnect(SubscriptionSession session, OperationMessage message) {
    @SuppressWarnings("unchecked")
    Map<String, Object> connectionParams = (Map<String, Object>) message.getPayload();
    session.getUserProperties().put("connectionParams", connectionParams);
  }
}
