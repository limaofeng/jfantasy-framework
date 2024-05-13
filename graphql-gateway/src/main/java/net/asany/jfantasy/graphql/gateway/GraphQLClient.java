package net.asany.jfantasy.graphql.gateway;

import graphql.language.Document;
import java.io.IOException;
import java.util.function.Function;
import net.asany.jfantasy.graphql.client.GraphQLResponse;
import net.asany.jfantasy.graphql.client.QueryPayload;
import org.reactivestreams.Publisher;

public interface GraphQLClient {
  Document introspectionQuery() throws IOException;

  GraphQLResponse query(QueryPayload payload, String token) throws IOException;

  Publisher<Object> subscribe(
      QueryPayload payload, String token, Function<Object, Object> converter);

  void connect();
}
