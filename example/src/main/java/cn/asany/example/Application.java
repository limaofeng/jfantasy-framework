package cn.asany.example;

import cn.asany.example.demo.domain.UserSetting;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.jpa.SimpleAnyJpaRepository;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.AuthenticationToken;
import net.asany.jfantasy.framework.security.auth.core.*;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsService;
import net.asany.jfantasy.graphql.SchemaParserDictionaryBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 启动器
 *
 * @author limaofeng
 * @version V1.0
 */
@Slf4j
@EnableCaching
@Configuration
@ComponentScan("cn.asany.example.demo")
@EntityScan({"cn.asany.example.*.domain"})
@EnableJpaRepositories(
    includeFilters = {
      @ComponentScan.Filter(
          type = FilterType.ASSIGNABLE_TYPE,
          value = {JpaRepository.class})
    },
    basePackages = {
      "cn.asany.example.*.dao",
    },
    repositoryBaseClass = SimpleAnyJpaRepository.class)
@EnableAutoConfiguration(exclude = {MongoAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
    return builder.sources(Application.class);
  }

  @Bean
  public SchemaParserDictionaryBuilder dictionaryBuilder() {
    return dictionary -> dictionary.add("DemoUserSettings", UserSetting.class);
  }

  @Bean
  public UserDetailsService<LoginUser> userDetailsService() {
    return (tid, username) -> LoginUser.builder().build();
  }

  @Bean
  public ClientDetailsService clientDetailsService() {
    return clientId ->
        new ClientDetails() {
          @Override
          public Map<String, Object> getAdditionalInformation() {
            return null;
          }

          @Override
          public <T> T getAdditionalInformation(String key, Class<T> clazz) {
            return null;
          }

          @Override
          public Long getId() {
            return 12L;
          }

          @Override
          public String getName() {
            return "";
          }

          @Override
          public Collection<GrantedAuthority> getAuthorities() {
            return null;
          }

          @Override
          public Set<String> getAuthorizedGrantTypes() {
            return null;
          }

          @Override
          public String getClientId() {
            return null;
          }

          @Override
          public Set<ClientSecret> getClientSecrets(ClientSecretType type) {
            return new HashSet<>();
          }

          @Override
          public Optional<ClientSecret> getClientSecret(String id) {
            return Optional.empty();
          }

          @Override
          public Optional<ClientSecret> getClientSecret(ClientSecretType type) {
            return Optional.empty();
          }

          @Override
          public String getRedirectUri() {
            return null;
          }

          @Override
          public Set<String> getScope() {
            return null;
          }
        };
  }

  @Bean
  public TokenStore<OAuth2AccessToken> defaultTokenStore() {
    return new TokenStore<>() {
      @Override
      public BearerTokenAuthentication readAuthentication(AuthenticationToken<String> token) {
        return null;
      }

      @Override
      public AuthenticationToken<OAuth2AccessToken> readAuthentication(String token) {
        return null;
      }

      @Override
      public void storeAccessToken(OAuth2AccessToken token, Authentication authentication) {}

      @Override
      public OAuth2AccessToken readAccessToken(String tokenValue) {
        return null;
      }

      @Override
      public void removeAccessToken(OAuth2AccessToken token) {}

      @Override
      public void storeRefreshToken(AuthRefreshToken refreshToken, Authentication authentication) {}

      @Override
      public AuthRefreshToken readRefreshToken(String tokenValue) {
        return null;
      }

      @Override
      public BearerTokenAuthentication readAuthenticationForRefreshToken(AuthRefreshToken token) {
        return null;
      }

      @Override
      public void removeRefreshToken(AuthRefreshToken token) {}

      @Override
      public void removeAccessTokenUsingRefreshToken(AuthRefreshToken refreshToken) {}

      @Override
      public OAuth2AccessToken getAccessToken(AuthenticationToken authentication) {
        return null;
      }

      @Override
      public Collection<AuthToken> findTokensByClientIdAndUserName(
          String clientId, String userName) {
        return null;
      }

      @Override
      public Collection<AuthToken> findTokensByClientId(String clientId) {
        return null;
      }
    };
  }
}
