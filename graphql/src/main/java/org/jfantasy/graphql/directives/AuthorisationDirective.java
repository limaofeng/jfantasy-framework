package org.jfantasy.graphql.directives;

import graphql.schema.*;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;
import org.jfantasy.framework.util.common.ClassUtil;

/**
 * @author limaofeng
 * @version V1.0
 * @date 2019-04-13 22:01
 */
public class AuthorisationDirective implements SchemaDirectiveWiring {

  @Override
  public GraphQLFieldDefinition onField(
      SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
    InputValueWithState targetAuthRole =
        environment.getDirective().getArgument("requires").getArgumentValue();

    GraphQLFieldDefinition field = environment.getElement();
    //
    // build a data fetcher that first checks authorisation roles before then
    // calling the original
    // data fetcher
    // field.getDataFetcher()  不是 public 的
    DataFetcher originalDataFetcher = ClassUtil.call("getDataFetcher", field);
    DataFetcher authDataFetcher =
        new DataFetcher() {
          @Override
          public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
            Object contextMap = dataFetchingEnvironment.getGraphQlContext();
            System.out.println(contextMap);
            return originalDataFetcher.get(dataFetchingEnvironment);
          }
        };
    //
    // now change the field definition to have the new authorising data fetcher
    return field.transform(builder -> builder.dataFetcher(authDataFetcher));
  }
}
