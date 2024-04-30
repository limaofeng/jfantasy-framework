package net.asany.jfantasy.graphql.gateway.service;

import graphql.schema.*;
import java.io.IOException;

public interface GraphQLService {

  String getName();

  GraphQLSchema makeSchema() throws IOException;
}
