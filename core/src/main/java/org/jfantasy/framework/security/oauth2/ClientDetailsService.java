package org.jfantasy.framework.security.oauth2;

public interface ClientDetailsService {
    ClientDetails loadClientByClientId(String clientId)
        throws ClientRegistrationException;
}
