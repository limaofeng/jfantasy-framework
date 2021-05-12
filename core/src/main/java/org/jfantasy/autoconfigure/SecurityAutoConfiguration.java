package org.jfantasy.autoconfigure;

import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author limaofeng
 */
@Configuration
public class SecurityAutoConfiguration {

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> providers) {
        AuthenticationManager authenticationManager = new AuthenticationManager();
        for (AuthenticationProvider provider : providers) {
            authenticationManager.addProvider(provider);
        }
        return authenticationManager;
    }

    @Bean
    @ConditionalOnBean({UserDetailsService.class, PasswordEncoder.class})
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        return new DaoAuthenticationProvider();
    }

}
