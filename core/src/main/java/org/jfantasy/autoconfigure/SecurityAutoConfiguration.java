package org.jfantasy.autoconfigure;

import org.jfantasy.framework.security.AuthenticationManager;
import org.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.jfantasy.framework.security.authentication.dao.DaoAuthenticationProvider;
import org.jfantasy.framework.security.core.userdetails.UserDetailsService;
import org.jfantasy.framework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import javax.sql.XADataSource;
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
    @ConditionalOnMissingBean({ DaoAuthenticationProvider.class })
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService,PasswordEncoder passwordEncoder) {
        return new DaoAuthenticationProvider(userDetailsService, passwordEncoder);
    }

}
