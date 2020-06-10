package org.jfantasy.graphql.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import graphql.language.SchemaExtensionDefinition;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.jfantasy.framework.jackson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Component
@ConditionalOnProperty(prefix = "graphql.server", name = "url")
public class GraphQLClient {
    @Autowired
    private ResourceLoader resourceLoader;
    @Autowired(required = false)
    private RestTemplate restTemplate;
    @Value("${graphql.server.url}")
    private String graphQLServerUrl;
    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders headers = new HttpHeaders();

    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setHeaders(HttpHeaders newHeaders) {
        headers = newHeaders;
    }

    public void clearHeaders() {
        setHeaders(new HttpHeaders());
    }

    public GraphQLResponse perform(String graphqlResource) throws IOException {
        return postForResource(graphqlResource);
    }

    public GraphQLResponse perform(String graphqlResource, ObjectNode variables) throws IOException {
        String graphql = loadQuery(graphqlResource);
        String payload = createJsonQuery(graphql, variables);
        return post(payload);
    }

    public GraphQLResponse perform(String graphqlResource, Map<String, Object> paramMap)
            throws IOException {
        String graphql = loadQuery(graphqlResource);
        String payload = createJsonQuery(graphql, paramMap);
        return post(payload);
    }


    public GraphQLResponse perform(String graphqlResource, ObjectNode variables, List<String> fragmentResources) throws IOException {
        String graphql = getGraphql(graphqlResource, fragmentResources);
        String payload = createJsonQuery(graphql, variables);
        return post(payload);
    }


    public GraphQLResponse perform(String graphqlResource, Map<String, Object> paramMap, List<String> fragmentResources) throws IOException {
        String graphql = getGraphql(graphqlResource, fragmentResources);
        String payload = createJsonQuery(graphql, paramMap);
        return post(payload);
    }


    public GraphQLResponse postForResource(String graphqlResource) throws IOException {
        return perform(graphqlResource, (ObjectNode) null);
    }

    public GraphQLResponse postForResource(String graphqlResource, List<String> fragmentResources) throws IOException {
        return perform(graphqlResource, (ObjectNode) null, fragmentResources);
    }

    public GraphQLResponse postMultipart(String query, String variables) {
        return postRequest(RequestFactory.forMultipart(query, variables, headers));
    }

    public GraphQLResponse postMultipart(String query, Map<String, Object> paramMap) {
        return postMultipart(query, JSON.serialize(paramMap));
    }


    private GraphQLResponse post(String payload) {
        return postRequest(RequestFactory.forJson(payload, headers));
    }

    private GraphQLResponse postRequest(HttpEntity<Object> request) {
        ResponseEntity<String> response = restTemplate.exchange(graphQLServerUrl, HttpMethod.POST, request, String.class);
        return new GraphQLResponse(response, objectMapper);
    }

    private String createJsonQuery(String graphql, ObjectNode variables)
            throws JsonProcessingException {
        ObjectNode wrapper = objectMapper.createObjectNode();
        wrapper.put("query", graphql);
        wrapper.set("variables", variables);
        return objectMapper.writeValueAsString(wrapper);
    }

    private String createJsonQuery(String graphql, Map<String, Object> paramMap)
            throws JsonProcessingException {
        ObjectNode wrapper = objectMapper.createObjectNode();
        wrapper.put("query", graphql);
        wrapper.putPOJO("variables", paramMap);
        return objectMapper.writeValueAsString(wrapper);
    }

    private String loadQuery(String location) throws IOException {
        Resource resource = resourceLoader.getResource("classpath:" + location);
        return loadResource(resource);
    }

    private String loadResource(Resource resource) throws IOException {
        try (InputStream inputStream = resource.getInputStream()) {
            return StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
        }
    }

    private String getGraphql(String graphqlResource, List<String> fragmentResources) throws IOException {
        StringBuilder sb = new StringBuilder();
        for (String fragmentResource : fragmentResources) {
            sb.append(loadQuery(fragmentResource));
        }
        return sb.append(loadQuery(graphqlResource)).toString();
    }
}
