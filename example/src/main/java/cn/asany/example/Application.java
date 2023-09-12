package cn.asany.example;

import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.jpa.SimpleAnyJpaRepository;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.core.GrantedAuthority;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.jfantasy.framework.security.crypto.password.PlaintextPasswordEncoder;
import org.jfantasy.framework.security.oauth2.DefaultTokenServices;
import org.jfantasy.framework.security.oauth2.core.*;
import org.jfantasy.framework.security.oauth2.server.BearerTokenAuthenticationToken;
import org.jfantasy.framework.security.oauth2.server.authentication.BearerTokenAuthentication;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * 启动器
 *
 * @author limaofeng
 * @version V1.0
 * @date 2019/2/13 4:04 PM
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
  public UserDetailsService userDetailsService() {
    return new UserDetailsService() {

      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return LoginUser.builder().build();
      }
    };
  }

  @Bean
  public DefaultTokenServices defaultTokenServices(TaskExecutor taskExecutor) {
    return new DefaultTokenServices(
        new TokenStore() {
          @Override
          public BearerTokenAuthentication readAuthentication(
              BearerTokenAuthenticationToken token) {
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
          public void storeRefreshToken(
              OAuth2RefreshToken refreshToken, Authentication authentication) {}

          @Override
          public OAuth2RefreshToken readRefreshToken(String tokenValue) {
            return null;
          }

          @Override
          public BearerTokenAuthentication readAuthenticationForRefreshToken(
              OAuth2RefreshToken token) {
            return null;
          }

          @Override
          public void removeRefreshToken(OAuth2RefreshToken token) {}

          @Override
          public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {}

          @Override
          public OAuth2AccessToken getAccessToken(BearerTokenAuthentication authentication) {
            return null;
          }

          @Override
          public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(
              String clientId, String userName) {
            return null;
          }

          @Override
          public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
            return null;
          }
        },
        new ClientDetailsService() {
          @Override
          public ClientDetails loadClientByClientId(String clientId)
              throws ClientRegistrationException {
            return new ClientDetails() {
              @Override
              public Map<String, Object> getAdditionalInformation() {
                return new HashMap<>();
              }

              @Override
              public Collection<GrantedAuthority> getAuthorities() {
                return new ArrayList<>();
              }

              @Override
              public Set<String> getAuthorizedGrantTypes() {
                return null;
              }

              @Override
              public String getClientId() {
                return "111";
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
              public Integer getTokenExpires() {
                return 0;
              }
            };
          }
        },
        taskExecutor);
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new PlaintextPasswordEncoder();
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
