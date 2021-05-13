package org.jfantasy.autoconfigure;

import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.authentication.AuthenticationEventPublisher;
import org.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.jfantasy.framework.security.authentication.DefaultAuthenticationEventPublisher;
import org.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
    @ConditionalOnBean({UserDetailsService.class, PasswordEncoder.class})
    @ConditionalOnMissingBean({DaoAuthenticationProvider.class})
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        return new DaoAuthenticationProvider(userDetailsService, passwordEncoder);
    }

}
