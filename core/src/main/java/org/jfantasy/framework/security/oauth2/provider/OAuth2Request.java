package org.jfantasy.framework.security.oauth2.provider;

import lombok.Data;
import org.jfantasy.framework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.*;

@Data
public class OAuth2Request extends BaseRequest {

    private Set<String> resourceIds = new HashSet<String>();

    private Collection<? extends GrantedAuthority> authorities = new HashSet<GrantedAuthority>();

    private boolean approved = false;

    private TokenRequest refresh = null;

    private String redirectUri;

    private Set<String> responseTypes = new HashSet<String>();

    private Map<String, Serializable> extensions = new HashMap<String, Serializable>();


    public OAuth2Request createOAuth2Request(Map<String, String> combinedParameters) {
        return null;
    }
}
