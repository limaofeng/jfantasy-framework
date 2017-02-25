package org.jfantasy.autoconfigure;

import org.jfantasy.member.ProfileFactory;
import org.jfantasy.member.ProfileFactoryBean;
import org.jfantasy.member.bean.Member;
import org.jfantasy.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@ComponentScan({"org.jfantasy.member", "org.jfantasy.sns"})
@Configuration
@EntityScan({"org.jfantasy.member.bean", "org.jfantasy.sns.bean"})
public class MemberAutoConfiguration {

    @Bean
    public ProfileFactory profileFactory(@Autowired MemberService memberService) {
        ProfileFactory factory = new ProfileFactoryBean();
        factory.register(Member.MEMBER_TYPE_PERSONAL, memberService);
        return factory;
    }

}
