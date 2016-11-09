package org.jfantasy.security.data;

import org.jfantasy.oauth.UrlResource;
import org.jfantasy.oauth.userdetails.OAuthUserDetails;
import org.jfantasy.security.matcher.UrlResourceRequestMatcher;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultSecurityStorage implements SecurityStorage {

    private RedisTemplate redisTemplate;

    public DefaultSecurityStorage(RedisTemplate redisTemplate){
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public OAuthUserDetails loadUserByUsername(String token) {
        HashOperations<String,String,OAuthUserDetails> hashOper = redisTemplate.opsForHash();
        OAuthUserDetails userDetails = hashOper.get(SecurityStorage.ASSESS_TOKEN_PREFIX + token, "user");
        if (userDetails == null) {
            throw new UsernameNotFoundException(" Token Invalid ");
        }
        return userDetails;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public UrlResourceRequestMatcher[] getRequestMatchers() {
        ValueOperations<String, UrlResource> valueOper = redisTemplate.opsForValue();
        SetOperations<String,String> setOper = redisTemplate.opsForSet();

        List<UrlResourceRequestMatcher> requestMatchers = valueOper.multiGet(setOper.members(SecurityStorage.RESOURCE_IDS)).stream().map(UrlResourceRequestMatcher::new).collect(Collectors.toList());
        return requestMatchers.toArray(new UrlResourceRequestMatcher[requestMatchers.size()]);
    }

    @Override
    public Collection<ConfigAttribute> getAllPermissions() {
        return new ArrayList<>();
    }


}
