package net.asany.jfantasy.framework.security.auth.utils;

public class AuthTokenUtils {

  //  public static Authentication simple(LoginUser principal) {
  //    SimpleAuthenticationToken<?> authentication = new SimpleAuthenticationToken<>(principal);
  //    authentication.setDetails(DefaultAuthenticationDetails.builder().build());
  //    return authentication;
  //  }
  //
  //  public static Authentication simple(ApiKeyPrincipal principal) {
  //    SimpleAuthenticationToken<?> authentication = new SimpleAuthenticationToken<>(principal);
  //    authentication.setDetails(DefaultAuthenticationDetails.builder().build());
  //    return authentication;
  //  }

  //  public static ApiKeyAuthentication buildApiKey(String clientId, ApiKeyPrincipal principal) {
  //    AuthenticationDetails authenticationDetails =
  //        DefaultAuthenticationDetails.builder()
  //            .clientId(clientId)
  //            .tokenType(TokenType.API_KEY)
  //            .build();
  //    return new ApiKeyAuthentication(AuthTokenUtils.simple(principal), authenticationDetails);
  //  }
}
