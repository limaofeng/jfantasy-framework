package cn.asany.example;

import cn.asany.example.demo.domain.UserSetting;
import com.fasterxml.jackson.annotation.JsonInclude;
import graphql.kickstart.execution.GraphQLObjectMapper;
import graphql.kickstart.tools.SchemaParser;
import java.io.IOException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.jpa.SimpleAnyJpaRepository;
import net.asany.jfantasy.framework.security.LoginUser;
import net.asany.jfantasy.framework.security.auth.TokenType;
import net.asany.jfantasy.framework.security.auth.core.*;
import net.asany.jfantasy.framework.security.auth.oauth2.core.OAuth2AccessToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.BearerTokenAuthenticationToken;
import net.asany.jfantasy.framework.security.auth.oauth2.server.authentication.BearerTokenAuthentication;
import net.asany.jfantasy.framework.security.authentication.Authentication;
import net.asany.jfantasy.framework.security.core.GrantedAuthority;
import net.asany.jfantasy.framework.security.core.userdetails.UserDetailsService;
import net.asany.jfantasy.framework.security.crypto.password.PasswordEncoder;
import net.asany.jfantasy.framework.security.crypto.password.PlaintextPasswordEncoder;
import net.asany.jfantasy.graphql.SchemaParserDictionaryBuilder;
import net.asany.jfantasy.graphql.gateway.GraphQLClientFactory;
import net.asany.jfantasy.graphql.gateway.GraphQLGateway;
import net.asany.jfantasy.graphql.gateway.GraphQLGatewayReloadSchemaProvider;
import net.asany.jfantasy.graphql.gateway.GraphQLReloadSchemaProvider;
import net.asany.jfantasy.graphql.gateway.service.DefaultGraphQLClientFactory;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeProviderFactory;
import net.asany.jfantasy.graphql.gateway.type.ScalarTypeResolver;
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
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

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
    return dictionary -> {
      dictionary.add("DemoUserSettings", UserSetting.class);
    };
  }

  @Bean
  public UserDetailsService<LoginUser> userDetailsService() {
    return username -> LoginUser.builder().build();
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
          public Set<String> getClientSecrets(ClientSecretType type) {
            return new HashSet<>();
          }

          @Override
          public String getRedirectUri() {
            return null;
          }

          @Override
          public Set<String> getScope() {
            return null;
          }

          @Override
          public Integer getTokenExpires(TokenType tokenType) {
            return 30;
          }
        };
  }

  @Bean
  public TokenStore<OAuth2AccessToken> defaultTokenStore() {
    return new TokenStore<>() {
      @Override
      public BearerTokenAuthentication readAuthentication(BearerTokenAuthenticationToken token) {
        return null;
      }

      @Override
      public BearerTokenAuthentication readAuthentication(String token) {
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
      public OAuth2AccessToken getAccessToken(BearerTokenAuthentication authentication) {
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

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PlaintextPasswordEncoder();
  }

  @Bean
  public GraphQLClientFactory graphQLTemplateFactory(
      ResourceLoader resourceLoader, RestTemplate restTemplate, GraphQLObjectMapper objectMapper) {
    return new DefaultGraphQLClientFactory(
        resourceLoader,
        restTemplate,
        objectMapper
            .getJacksonMapper()
            .copy()
            .setSerializationInclusion(JsonInclude.Include.ALWAYS));
  }

  @Bean(initMethod = "init", destroyMethod = "destroy")
  public GraphQLGateway graphqlGateway(
      SchemaParser schemaParser,
      GraphQLClientFactory templateFactory,
      ScalarTypeProviderFactory scalarFactory)
      throws IOException {
    return GraphQLGateway.builder()
        .schema(schemaParser.makeExecutableSchema())
        .clientFactory(templateFactory)
        .scalarResolver(new ScalarTypeResolver(scalarFactory))
        .config("classpath:graphql-gateway.yaml")
        .build();
  }

  @Bean
  public GraphQLReloadSchemaProvider graphqlSchemaProvider(GraphQLGateway graphQLGateway) {
    return new GraphQLGatewayReloadSchemaProvider(graphQLGateway);
  }

  //  @Bean
  //  public ShardingStrategyCustomizer shardingStrategyCustomizer() {
  //    return conf -> {
  //      Collection<TableRuleConfiguration> tableRuleConfigs = conf.getTableRuleConfigs();
  //      // table rule
  //      TableRuleConfiguration table2Rule = new TableRuleConfiguration("sys_user");
  //
  //      MyDatabaseShardingAlgorithm myDatabaseShardingAlgorithm = new
  // MyDatabaseShardingAlgorithm();
  //      ShardingStrategyConfiguration shardingStrategyConfiguration =
  //          new StandardShardingStrategyConfiguration("username", myDatabaseShardingAlgorithm);
  //      table2Rule.setDatabaseShardingStrategyConfig(shardingStrategyConfiguration);
  //      tableRuleConfigs.add(table2Rule);
  //      // key生成规则
  //      //    KeyGeneratorConfiguration key2Gen = new KeyGeneratorConfiguration("PUSHINFODETAIL",
  //      // "id");
  //      //    table2Rule.setKeyGeneratorConfig(key2Gen);
  //
  //      StandardShardingStrategyConfiguration databaseShardingStrategy =
  //          new StandardShardingStrategyConfiguration("username", myDatabaseShardingAlgorithm);
  //
  //      // 分表策略
  //      //      ShardingStrategyConfiguration tableSharding2StrategyConfig = new
  //      // InlineShardingStrategyConfiguration("info_type", "t_wx_push_info_details$->{info_type %
  // 8 +
  //      // 1}");
  //      //      table2Rule.setTableShardingStrategyConfig(databaseShardingStrategy);
  //      //      tableRuleConfigs.add(table2Rule);
  //
  //      conf.setDefaultDatabaseShardingStrategyConfig(databaseShardingStrategy);
  //    };
  //  }

  //  @Bean
  //  public MultiDataSourceManager dataSourceManager() {
  //    Map xxx = new HashMap() {};
  //
  //    //    return new MultiDataSourceManager() {
  //    //
  //    //      @Override
  //    //      public DataSource getDataSource(String dataSourceKey) {
  //    //        return (DataSource) xxx.get(dataSourceKey);
  //    //      }
  //    //
  //    //      @Override
  //    //      public Map getAllDataSources() {
  //    //        return xxx;
  //    //      }
  //    //    };
  //
  //    return new AbstractMultiDataSourceManager(new HashMap<>());
  //  }
}
