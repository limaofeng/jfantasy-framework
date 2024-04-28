package net.asany.jfantasy.graphql.client;

import static java.util.Objects.nonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

public class GraphQLTemplate {

  private final ResourceLoader resourceLoader;
  private final RestTemplate restTemplate;
  private final String graphqlMapping;
  private final ObjectMapper objectMapper;
  private final HttpHeaders headers = new HttpHeaders();

  public GraphQLTemplate(
      final ResourceLoader resourceLoader,
      final RestTemplate restTemplate,
      @Value("${graphql.servlet.mapping:/graphql}") final String graphqlMapping,
      final ObjectMapper objectMapper) {
    this.resourceLoader = resourceLoader;
    this.restTemplate = restTemplate;
    this.graphqlMapping = graphqlMapping;
    this.objectMapper = objectMapper;
  }

  private String createJsonQuery(String graphql, String operation, ObjectNode variables)
      throws JsonProcessingException {

    ObjectNode wrapper = objectMapper.createObjectNode();
    wrapper.put("query", graphql);
    if (nonNull(operation)) {
      wrapper.put("operationName", operation);
    }
    wrapper.set("variables", variables);
    return objectMapper.writeValueAsString(wrapper);
  }

  private MultipartQuery createMultipartQuery(
      String graphql, String operation, Map<String, Object> variables)
      throws JsonProcessingException {
    ObjectNode wrapper = objectMapper.createObjectNode();
    wrapper.put("query", graphql);
    if (nonNull(operation)) {
      wrapper.put("operationName", operation);
    }

    Map<String, Part> parts = new HashMap<>();

    Map<String, Object> newVariables = new HashMap<>(variables);
    for (Map.Entry<String, Object> entry : newVariables.entrySet()) {
      if (entry.getValue() instanceof Part part) {
        parts.put("variables." + entry.getKey(), part);
        entry.setValue(null);
      }
    }

    wrapper.set("variables", this.objectMapper.valueToTree(newVariables));

    return MultipartQuery.builder()
        .operations(objectMapper.writeValueAsString(wrapper))
        .parts(parts)
        .build();
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

  /**
   * Add an HTTP header that will be sent with each request this sends.
   *
   * @param name Name (key) of HTTP header to add.
   * @param value Value(s) of HTTP header to add.
   * @return self
   */
  public GraphQLTemplate withAdditionalHeader(final String name, final String... value) {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.addAll(name, Arrays.asList(value));
    return newTemplate;
  }

  /**
   * Add multiple HTTP header that will be sent with each request this sends.
   *
   * @param additionalHeaders additional headers to add
   * @return self
   */
  public GraphQLTemplate withAdditionalHeaders(
      final MultiValueMap<String, String> additionalHeaders) {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.addAll(additionalHeaders);
    return newTemplate;
  }

  /**
   * Adds a bearer token to the authorization header.
   *
   * @param token the bearer token
   * @return self
   */
  public GraphQLTemplate withBearerAuth(@NonNull final String token) {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.setBearerAuth(token);
    return newTemplate;
  }

  /**
   * Adds basic authentication to the authorization header.
   *
   * @param username the username
   * @param password the password
   * @param charset the charset used by the credentials
   * @return self
   */
  public GraphQLTemplate withBasicAuth(
      @NonNull final String username,
      @NonNull final String password,
      @Nullable final Charset charset) {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.setBasicAuth(username, password, charset);
    return newTemplate;
  }

  /**
   * Adds basic authentication to the authorization header.
   *
   * @param username the username
   * @param password the password
   * @return self
   */
  public GraphQLTemplate withBasicAuth(
      @NonNull final String username, @NonNull final String password) {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.setBasicAuth(username, password, null);
    return newTemplate;
  }

  /**
   * Adds basic authentication to the authorization header.
   *
   * @param encodedCredentials the encoded credentials
   * @return self
   */
  public GraphQLTemplate withBasicAuth(@NonNull final String encodedCredentials) {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.setBasicAuth(encodedCredentials);
    return newTemplate;
  }

  /**
   * Replace any associated HTTP headers with the provided headers.
   *
   * @param newHeaders Headers to use.
   * @return self
   */
  public GraphQLTemplate withHeaders(final HttpHeaders newHeaders) {
    GraphQLTemplate newTemplate = withClearHeaders();
    newTemplate.setDefaultHeaders(newHeaders);
    return newTemplate;
  }

  private GraphQLTemplate newTemplate() {
    GraphQLTemplate newTemplate =
        new GraphQLTemplate(resourceLoader, restTemplate, graphqlMapping, objectMapper);
    newTemplate.setDefaultHeaders(headers);
    return newTemplate;
  }

  public void setDefaultHeaders(HttpHeaders headers) {
    this.headers.clear();
    this.headers.addAll(headers);
  }

  /**
   * Clear all associated HTTP headers.
   *
   * @return self
   */
  public GraphQLTemplate withClearHeaders() {
    GraphQLTemplate newTemplate = this.newTemplate();
    newTemplate.headers.clear();
    return newTemplate;
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param variables the input variables for the GraphQL query
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(String graphqlResource, ObjectNode variables) throws IOException {
    return perform(graphqlResource, null, variables, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param operationName the name of the GraphQL operation to be executed
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(String graphqlResource, String operationName) throws IOException {
    return perform(graphqlResource, operationName, null, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param operation the name of the GraphQL operation to be executed
   * @param variables the input variables for the GraphQL query
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(String graphqlResource, String operation, ObjectNode variables)
      throws IOException {
    return perform(graphqlResource, operation, variables, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param variables the input variables for the GraphQL query
   * @param fragmentResources an ordered list of classpath resources containing GraphQL fragment
   *     definitions.
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(
      String graphqlResource, ObjectNode variables, List<String> fragmentResources)
      throws IOException {
    return perform(graphqlResource, null, variables, fragmentResources);
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param variables the input variables for the GraphQL query
   * @param fragmentResources an ordered list of classpath resources containing GraphQL fragment
   *     definitions.
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse perform(
      String graphqlResource,
      String operationName,
      ObjectNode variables,
      List<String> fragmentResources)
      throws IOException {
    StringBuilder sb = new StringBuilder();
    for (String fragmentResource : fragmentResources) {
      sb.append(loadQuery(fragmentResource));
    }
    String graphql = sb.append(loadQuery(graphqlResource)).toString();
    String payload = createJsonQuery(graphql, operationName, variables);
    return post(payload);
  }

  public GraphQLResponse post(String graphql, String operationName) throws IOException {
    String payload = createJsonQuery(graphql, operationName, null);
    return postRequest(RequestFactory.forJson(payload, headers));
  }

  public GraphQLResponse post(String graphql, String operationName, Map<String, Object> variables)
      throws IOException {

    boolean isUploaded = variables.values().stream().anyMatch(val -> val instanceof Part);

    if (isUploaded) {
      MultipartQuery query = createMultipartQuery(graphql, operationName, variables);
      return postRequest(
          RequestFactory.forMultipart(query.getOperations(), query.getParts(), headers));
    }

    String payload =
        createJsonQuery(graphql, operationName, this.objectMapper.valueToTree(variables));
    return postRequest(RequestFactory.forJson(payload, headers));
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource and sends it to the GraphQL
   * server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse postForResource(String graphqlResource) throws IOException {
    return perform(graphqlResource, null, null, Collections.emptyList());
  }

  /**
   * Loads a GraphQL query or mutation from the given classpath resource, appending any graphql
   * fragment resources provided and sends it to the GraphQL server.
   *
   * @param graphqlResource path to the classpath resource containing the GraphQL query
   * @param fragmentResources an ordered list of classpath resources containing GraphQL fragment
   *     definitions.
   * @return {@link GraphQLResponse} containing the result of query execution
   * @throws IOException if the resource cannot be loaded from the classpath
   */
  public GraphQLResponse postForResource(String graphqlResource, List<String> fragmentResources)
      throws IOException {
    return perform(graphqlResource, null, null, fragmentResources);
  }

  public GraphQLResponse postMultipart(String query, Map<String, Part> payload) {
    return postRequest(RequestFactory.forMultipart(query, payload, headers));
  }

  /**
   * Performs a GraphQL request with the provided payload.
   *
   * @param payload the GraphQL payload
   * @return @return {@link GraphQLResponse} containing the result of query execution
   */
  public GraphQLResponse post(String payload) {
    return postRequest(RequestFactory.forJson(payload, headers));
  }

  private GraphQLResponse postRequest(HttpEntity<Object> request) {
    ResponseEntity<String> response =
        restTemplate.exchange(graphqlMapping, HttpMethod.POST, request, String.class);
    return new GraphQLResponse(response, objectMapper);
  }

  @Data
  @Builder
  public static class MultipartQuery {
    private String operations;
    private Map<String, Part> parts;
  }
}
