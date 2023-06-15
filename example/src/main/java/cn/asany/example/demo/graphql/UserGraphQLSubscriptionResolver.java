package cn.asany.example.demo.graphql;

import cn.asany.example.demo.domain.User;
import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserGraphQLSubscriptionResolver implements GraphQLSubscriptionResolver {

  private final StockTickerRxPublisher stockTickerPublisher;
  private final UserChangePublisher userChangePublisher;

  UserGraphQLSubscriptionResolver(
      StockTickerRxPublisher stockTickerPublisher, UserChangePublisher userChangePublisher) {
    this.userChangePublisher = userChangePublisher;
    this.stockTickerPublisher = stockTickerPublisher;
  }

  Publisher<StockPriceUpdate> stockQuotes(List<String> stockCodes) {
    return stockTickerPublisher.getPublisher(stockCodes);
  }

  Publisher<User> userChange() {
    return userChangePublisher.getPublisher();
  }
}
