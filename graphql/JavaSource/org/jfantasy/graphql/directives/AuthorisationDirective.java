package org.jfantasy.graphql.directives;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.idl.SchemaDirectiveWiring;
import graphql.schema.idl.SchemaDirectiveWiringEnvironment;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-13 22:01
 */
public class AuthorisationDirective implements SchemaDirectiveWiring {

    @Override
    public GraphQLFieldDefinition onField(SchemaDirectiveWiringEnvironment<GraphQLFieldDefinition> environment) {
        String targetAuthRole = (String) environment.getDirective().getArgument("requires").getValue();

        GraphQLFieldDefinition field = environment.getElement();
        //
        // build a data fetcher that first checks authorisation roles before then calling the original data fetcher
        //
        DataFetcher originalDataFetcher = field.getDataFetcher();
        DataFetcher authDataFetcher = new DataFetcher() {
            @Override
            public Object get(DataFetchingEnvironment dataFetchingEnvironment) throws Exception {
                Object contextMap = dataFetchingEnvironment.getContext();
                System.out.println(contextMap);
                return originalDataFetcher.get(dataFetchingEnvironment);
            }
        };
        //
        // now change the field definition to have the new authorising data fetcher
        return field.transform(builder -> builder.dataFetcher(authDataFetcher));
    }
}