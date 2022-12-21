package cn.asany.example.demo.graphql;

import graphql.kickstart.tools.GraphQLSubscriptionResolver;
import java.util.List;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;

@Component
public class UserGraphQLSubscriptionResolver implements GraphQLSubscriptionResolver {

  private StockTickerRxPublisher stockTickerPublisher;

  UserGraphQLSubscriptionResolver(StockTickerRxPublisher stockTickerPublisher) {
    this.stockTickerPublisher = stockTickerPublisher;
  }

  Publisher<StockPriceUpdate> stockQuotes(List<String> stockCodes) {
    return stockTickerPublisher.getPublisher(stockCodes);
  }
}
