package org.jfantasy.autoconfigure;

import com.coxautodev.graphql.tools.*;
import com.google.common.base.Charsets;
import graphql.GraphQL;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import org.apache.commons.io.IOUtils;
import org.jfantasy.graphql.GraphQLSchemaProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2019-04-01 13:55
 */
@Configuration
public class GraphQLJavaToolsAutoConfiguration {

    @Autowired(required = false)
    private SchemaParserDictionary dictionary;

    @Autowired(required = false)
    private GraphQLScalarType[] scalars;

    @Autowired(required = false)
    private SchemaParserOptions options;

    @Autowired(required = false)
    private ApplicationContext applicationContext;

    @Bean
    @ConditionalOnBean({GraphQLResolver.class})
    @ConditionalOnMissingBean
    public SchemaParser schemaParser(List<GraphQLResolver<?>> resolvers) throws IOException {
        SchemaParserBuilder builder = dictionary != null ? new SchemaParserBuilder(dictionary) : new SchemaParserBuilder();

        /**
         * 读取classpath下所有schema
         */
        Resource[] resources = applicationContext.getResources("classpath*:**/*.graphqls");
        if (resources.length <= 0) {
            throw new IllegalStateException("No *.graphqls files found on classpath.  Please add a graphql schema to the classpath or add a SchemaParser bean to your application context.");
        }

        for (Resource resource : resources) {
            StringWriter writer = new StringWriter();
            IOUtils.copy(resource.getInputStream(), writer, Charsets.UTF_8);
            builder.schemaString(writer.toString());
        }

        if (scalars != null) {
            builder.scalars(scalars);
        }

        if (options != null) {
            builder.options(options);
        }

        return builder.resolvers(resolvers).build();
    }

    @Bean
    @ConditionalOnBean(SchemaParser.class)
    @ConditionalOnMissingBean({GraphQLSchema.class, GraphQLSchemaProvider.class})
    public GraphQLSchema graphQLSchema(SchemaParser schemaParser) {
        return schemaParser.makeExecutableSchema();
    }

    @Bean
    public GraphQL graphQL(GraphQLSchema graphQLSchema) {
        return GraphQL.newGraphQL(graphQLSchema).build();
    }

}
