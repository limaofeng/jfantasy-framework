package net.asany.jfantasy.autoconfigure;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.HandshakeRequest;
import java.util.List;
import net.asany.jfantasy.framework.context.DatabaseMessageSource;
import net.asany.jfantasy.framework.context.service.LanguageService;
import net.asany.jfantasy.framework.security.AuthenticationManager;
import net.asany.jfantasy.framework.security.DefaultAuthenticationManagerResolver;
import net.asany.jfantasy.framework.security.WebFluxAuthenticationManagerResolver;
import net.asany.jfantasy.framework.security.WebSocketAuthenticationManagerResolver;
import net.asany.jfantasy.framework.security.authentication.*;
import net.asany.jfantasy.framework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.DefaultPostAuthenticationChecks;
import net.asany.jfantasy.framework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks;
import net.asany.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextBuilder;
import net.asany.jfantasy.framework.security.authorization.policy.context.RequestContextFactory;
import net.asany.jfantasy.framework.security.core.SecurityMessageSource;
import net.asany.jfantasy.framework.security.core.userdetails.*;
import net.asany.jfantasy.framework.security.crypto.password.PasswordEncoder;
import net.asany.jfantasy.framework.security.crypto.password.PlaintextPasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * OAuth2SecurityAutoConfiguration
 *
 * @author limaofeng
 */
@Configuration
public class SecurityAutoConfiguration {

  @Bean
  public RequestContextFactory requestContextFactory(List<RequestContextBuilder> builders) {
    return new RequestContextFactory(builders);
  }

  @Bean
  @ConditionalOnMissingBean({AuthenticationEventPublisher.class})
  public AuthenticationEventPublisher authenticationEventPublisher(
      ApplicationEventPublisher applicationEventPublisher) {
    return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
  }

  @Bean
  @ConditionalOnMissingBean({PasswordEncoder.class})
  public PasswordEncoder passwordEncoder() {
    return new PlaintextPasswordEncoder();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      List<AuthenticationProvider<? extends Authentication>> providers,
      @Autowired(required = false) AuthenticationEventPublisher publisher) {
    AuthenticationManager authenticationManager = new AuthenticationManager(providers);
    if (publisher != null) {
      authenticationManager.setAuthenticationEventPublisher(publisher);
    }
    return authenticationManager;
  }

  @Bean("authenticationManagerResolver")
  @ConditionalOnClass(EnableWebMvc.class)
  public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
      AuthenticationManager authenticationManager) {
    return new DefaultAuthenticationManagerResolver(authenticationManager);
  }

  @Bean("webFluxAuthenticationManagerResolver")
  @ConditionalOnClass(EnableWebFlux.class)
  public AuthenticationManagerResolver<ServerHttpRequest> webFluxAuthenticationManagerResolver(
      AuthenticationManager authenticationManager) {
    return new WebFluxAuthenticationManagerResolver(authenticationManager);
  }

  @Bean
  @ConditionalOnClass(HandshakeRequest.class)
  public AuthenticationManagerResolver<HandshakeRequest> webSocketAuthenticationManagerResolver(
      AuthenticationManager authenticationManager) {
    return new WebSocketAuthenticationManagerResolver(authenticationManager);
  }

  @Bean
  public DatabaseMessageSource messageSource(LanguageService languageService) {
    return new DatabaseMessageSource(languageService);
  }

  @Bean("securityMessageSource")
  public MessageSourceAccessor securityMessageSource(MessageSource messageSource) {
    MessageSourceAccessor messages = new MessageSourceAccessor(messageSource);
    SecurityMessageSource.setAccessor(messages);
    return messages;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean("pre.preUserDetailsCheckers")
  public UserDetailsChecker preUserDetailsCheckers(
      MessageSourceAccessor securityMessageSource, PreUserDetailsChecker[] checkers) {
    DefaultAuthenticationChecks checker =
        new DefaultAuthenticationChecks(new DefaultPreAuthenticationChecks(securityMessageSource));
    checker.addCheckers(checkers);
    return checker;
  }

  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
  @Bean("post.preUserDetailsCheckers")
  public UserDetailsChecker postUserDetailsCheckers(
      MessageSourceAccessor securityMessageSource, PostUserDetailsChecker[] checkers) {
    DefaultAuthenticationChecks checker =
        new DefaultAuthenticationChecks(new DefaultPostAuthenticationChecks(securityMessageSource));
    checker.addCheckers(checkers);
    return checker;
  }

  @Bean
  @ConditionalOnBean({UserDetailsService.class, PasswordEncoder.class})
  @ConditionalOnMissingBean({DaoAuthenticationProvider.class})
  public DaoAuthenticationProvider daoAuthenticationProvider(
      UserDetailsService<? extends UserDetails> userDetailsService,
      PasswordEncoder passwordEncoder,
      @Qualifier("pre.preUserDetailsCheckers") UserDetailsChecker preUserDetailsCheckers,
      @Qualifier("post.preUserDetailsCheckers") UserDetailsChecker postUserDetailsCheckers,
      MessageSourceAccessor securityMessageSource) {
    return new DaoAuthenticationProvider(
        userDetailsService,
        passwordEncoder,
        securityMessageSource,
        true,
        preUserDetailsCheckers,
        postUserDetailsCheckers);
  }
}
