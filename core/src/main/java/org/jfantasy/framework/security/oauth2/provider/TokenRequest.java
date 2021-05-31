package org.jfantasy.framework.security.oauth2.provider;

import lombok.Data;

import java.util.Collection;
import java.util.Map;

@Data
public class TokenRequest {
    Map<String, String> requestParameters;
    String clientId;
    Collection<String> scope;
    String grantType;

    protected TokenRequest() {
    }

    public TokenRequest(Map<String, String> requestParameters, String clientId, Collection<String> scope,
                        String grantType) {
        setClientId(clientId);
        setRequestParameters(requestParameters);
        setScope(scope);
        this.grantType = grantType;
    }
}
