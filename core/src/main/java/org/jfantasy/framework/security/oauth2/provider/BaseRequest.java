package org.jfantasy.framework.security.oauth2.provider;

import lombok.Data;

import java.util.*;

@Data
public class BaseRequest {
    private String clientId;
    private Set<String> scope = new HashSet<String>();

    private Map<String, String> requestParameters = Collections.unmodifiableMap(new HashMap<String, String>());
}
