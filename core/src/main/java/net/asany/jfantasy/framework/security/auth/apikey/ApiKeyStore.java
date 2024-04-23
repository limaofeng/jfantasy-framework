package net.asany.jfantasy.framework.security.auth.apikey;

import net.asany.jfantasy.framework.security.auth.core.AbstractTokenStore;
import org.springframework.data.redis.core.StringRedisTemplate;

public class ApiKeyStore extends AbstractTokenStore<ApiKey> {
  public ApiKeyStore(StringRedisTemplate redisTemplate) {
    super(redisTemplate, "api-key");
  }
}
