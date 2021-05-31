package org.jfantasy.framework.security.oauth2.provider;

import org.jfantasy.framework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.*;

public class AuthorizationRequest extends BaseRequest {
    private Map<String, String> approvalParameters = Collections.unmodifiableMap(new HashMap<String, String>());
    private String state;
    private Set<String> responseTypes = new HashSet<String>();
    private Set<String> resourceIds = new HashSet<String>();
    private Collection<? extends GrantedAuthority> authorities = new HashSet<GrantedAuthority>();
    private boolean approved = false;
    private String redirectUri;
    private Map<String, Serializable> extensions = new HashMap<String, Serializable>();
}