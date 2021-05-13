package org.jfantasy.graphql.context;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.framework.security.LoginUser;
import org.jfantasy.framework.security.core.userdetails.UserDetails;
import org.jfantasy.framework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author limaofeng
 */
@Component
@ConditionalOnClass(StringRedisTemplate.class)
public class RedisUserDetailsService implements SharedUserDetailsService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public UserDetails loadUserByToken(String token) throws UsernameNotFoundException {
        String value = redisTemplate.boundValueOps(token).get();
        if (!StringUtils.isEmpty(value)) {
            throw new UsernameNotFoundException("在 Redis 中未发现 token [" + token + "] 对应的用户信息");
        }
        Map<String, String> map = JSON.deserialize(value, HashMap.class);
        return JSON.deserialize(JSON.serialize(map.get("user")), LoginUser.class);
    }

}
