package org.jfantasy.framework.security;

import org.jfantasy.framework.security.authentication.Authentication;
import org.jfantasy.framework.security.authentication.AuthenticationProvider;
import org.jfantasy.framework.security.authentication.InternalAuthenticationServiceException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author limaofeng
 */
public class AuthenticationManager {

    private List<AuthenticationProvider> providers = new ArrayList<>();

    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Optional<AuthenticationProvider> optional = providers.stream().filter(item -> item.supports(authentication.getClass())).findFirst();
        if (!optional.isPresent()) {
            throw new InternalAuthenticationServiceException(authentication.getClass().getName() + " Provider NotFind");
        }
        AuthenticationProvider provider = optional.get();
        return provider.authenticate(authentication);
    }

    public void addProvider(AuthenticationProvider provider) {
        this.providers.add(provider);
    }
}
