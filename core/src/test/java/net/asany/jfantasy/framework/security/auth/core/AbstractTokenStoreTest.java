package net.asany.jfantasy.framework.security.auth.core;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collections;
import java.util.Set;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.core.AuthenticatedPrincipal;
import org.junit.jupiter.api.Test;

class AbstractTokenStoreTest {

  @Test
  void buildAuthToken() throws JsonProcessingException {
    ObjectMapper mapper =
        new ObjectMapper()
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .enable(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .enable(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .setSerializationInclusion(JsonInclude.Include.NON_EMPTY)
            .registerModule(new JavaTimeModule());

    String data =
        "{\"access_token\":{\"client_id\":\"jdc7t953mea633v9mzkc\",\"token_value\":\"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJodHRwczovL3d3dy5hc2FueS5jbiIsImV4cCI6MTcxMzg5MzgyMywiaWF0IjoxNzEzODkyMDE4LCJ1c2VyX2lkIjoxLCJjbGllbnRfaWQiOiJqZGM3dDk1M21lYTYzM3Y5bXprYyIsInRva2VuX3R5cGUiOiJTRVNTSU9OX0lEIn0.oiEhzEkbbuh_pqdsZnbl6e__-LN9kuMESsgYaWWt7LU\",\"issued_at\":\"2024-04-23T17:06:58.692527456Z\",\"expires_at\":\"2024-04-23T17:37:03.363306560Z\",\"token_type\":\"JWT\",\"principal_type\":\"net.asany.jfantasy.framework.security.LoginUser\"},\"principal\":{\"username\":\"admin\",\"password\":\"12345678\",\"uid\":1,\"type\":\"ADMIN\",\"name\":\"系统管理员\",\"avatar\":\"/r/29899/dcf4e6e660ec4329a836a5a08262c252.png\",\"enabled\":true,\"account_non_expired\":true,\"account_non_locked\":true,\"credentials_non_expired\":true,\"authorities\":[],\"tenant_id\":\"1691832353955123200\",\"_avatar\":{\"id\":\"wV8NpS54BeWPHP4_Gv0Lw0cN-uet3Q8Z\",\"name\":\"image.png\",\"mime_type\":\"image/png\",\"size\":0,\"path\":\"/r/29899/dcf4e6e660ec4329a836a5a08262c252.png\"}}}";
    JsonNode node = mapper.readTree(data);
    JsonNode accessTokenNode = node.get("access_token");
    JsonNode principalNode = node.get("principal");
    JsonNode authoritiesNode = node.get("authorities");

    Class<OAuth2AccessToken> authTokenClass = OAuth2AccessToken.class;

    OAuth2AccessToken authToken = mapper.treeToValue(accessTokenNode, authTokenClass);
    AuthenticatedPrincipal principal =
        mapper.treeToValue(principalNode, authToken.getPrincipalType());
    Set<String> authorities = mapper.convertValue(authoritiesNode, new TypeReference<>() {});

    TokenObject tokenObject =
        TokenObject.builder()
            .principal(principal)
            .authorities(authorities == null ? Collections.emptySet() : authorities)
            .accessToken(authToken)
            .build();
  }
}
