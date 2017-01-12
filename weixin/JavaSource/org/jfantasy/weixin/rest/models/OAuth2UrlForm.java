package org.jfantasy.weixin.rest.models;

import org.jfantasy.weixin.framework.oauth2.Scope;

public class OAuth2UrlForm {
    private String redirectUri;
    private Scope scope;
    private String state;

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
