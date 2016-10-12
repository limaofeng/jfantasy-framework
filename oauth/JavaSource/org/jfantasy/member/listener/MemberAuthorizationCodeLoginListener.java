package org.jfantasy.member.listener;

import org.jfantasy.member.bean.Member;
import org.jfantasy.member.event.LoginEvent;
import org.jfantasy.security.data.SecurityStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MemberAuthorizationCodeLoginListener implements ApplicationListener<LoginEvent> {

    private final RedisTemplate redisTemplate;

    @Autowired
    public MemberAuthorizationCodeLoginListener(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onApplicationEvent(LoginEvent event) {
        ValueOperations valueOper = redisTemplate.opsForValue();
        Member member = event.getMember();
        member.getAuthorities();// 防止因为 LAZY 导致的 hibernate 问题 , 这个方法会调用 getRoles() 与 getUserGroups() 方法
        member.setCode(UUID.randomUUID().toString());
        valueOper.set(SecurityStorage.AUTHORIZATION_CODE_PREFIX + member.getCode(), member);
    }

}
