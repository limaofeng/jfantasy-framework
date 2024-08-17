package net.asany.jfantasy.framework.security.auth.oauth2.server.web;

import java.util.List;
import net.asany.jfantasy.framework.security.auth.Token;
import net.asany.jfantasy.framework.security.auth.TokenResolver;

public class CompositeTokenResolver<T> {

  private final List<TokenResolver<T>> resolvers;

  @SafeVarargs
  public CompositeTokenResolver(TokenResolver<T>... resolvers) {
    this.resolvers = List.of(resolvers);
  }

  public Token resolveToken(T request) {
    for (TokenResolver<T> resolver : resolvers) {
      if (!resolver.supports(request)) {
        continue;
      }
      String token = resolver.resolve(request);
      if (token != null) {
        return new Token(resolver.getAuthType(), resolver.getAuthTokenType(), token);
      }
    }
    return null;
  }
}
