package org.jfantasy.autoconfigure;

import org.jfantasy.framework.context.DatabaseMessageSource;
import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.authentication.AuthenticationEventPublisher;
import org.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.jfantasy.framework.security.authentication.DefaultAuthenticationEventPublisher;
import org.jfantasy.framework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.jfantasy.framework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.DefaultPreAuthenticationChecks;
import org.jfantasy.framework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.DefaultPostAuthenticationChecks;
import org.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import org.jfantasy.framework.security.core.SecurityMessageSource;
import org.jfantasy.framework.security.core.userdetails.*;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.MessageSourceAccessor;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author limaofeng
 */
@Configuration
public class SecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean({AuthenticationEventPublisher.class})
    public AuthenticationEventPublisher authenticationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        return new DefaultAuthenticationEventPublisher(applicationEventPublisher);
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers, @Autowired(required = false) AuthenticationEventPublisher publisher) {
        AuthenticationManager authenticationManager = new AuthenticationManager();
        for (AuthenticationProvider provider : providers) {
            authenticationManager.addProvider(provider);
        }
        if (publisher != null) {
            authenticationManager.setAuthenticationEventPublisher(publisher);
        }
        return authenticationManager;
    }

    @Bean
    public DatabaseMessageSource messageSource() {
        return new DatabaseMessageSource();
    }

    @Bean("securityMessageSource")
    public MessageSourceAccessor securityMessageSource() {
        MessageSourceAccessor messages = new MessageSourceAccessor(messageSource());
        SecurityMessageSource.setAccessor(messages);
        return messages;
    }

    @Bean("pre.preUserDetailsCheckers")
    public UserDetailsChecker preUserDetailsCheckers(PreUserDetailsChecker[] checkers) {
        DefaultAuthenticationChecks checker = new DefaultAuthenticationChecks(new DefaultPreAuthenticationChecks(securityMessageSource()));
        checker.addCheckers(checkers);
        return checker;
    }

    @Bean("post.preUserDetailsCheckers")
    public UserDetailsChecker postUserDetailsCheckers(PostUserDetailsChecker[] checkers) {
        DefaultAuthenticationChecks checker = new DefaultAuthenticationChecks(new DefaultPostAuthenticationChecks(securityMessageSource()));
        checker.addCheckers(checkers);
        return checker;
    }

    @Bean
    @ConditionalOnBean({UserDetailsService.class, PasswordEncoder.class})
    @ConditionalOnMissingBean({DaoAuthenticationProvider.class})
    public DaoAuthenticationProvider daoAuthenticationProvider(
        UserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder,
        @Qualifier("pre.preUserDetailsCheckers") UserDetailsChecker preUserDetailsCheckers,
        @Qualifier("post.preUserDetailsCheckers") UserDetailsChecker postUserDetailsCheckers
    ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService, passwordEncoder);
        provider.setMessages(securityMessageSource());
        provider.setPreAuthenticationChecks(preUserDetailsCheckers);
        provider.setPostAuthenticationChecks(postUserDetailsCheckers);
        return provider;
    }

}
