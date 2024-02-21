package net.asany.jfantasy.framework.security.auth.oauth2.server;

public interface BearerTokenErrorCodes {

  String INVALID_REQUEST = "invalid_request";

  String INVALID_TOKEN = "invalid_token";

  String INSUFFICIENT_SCOPE = "insufficient_scope";
}
