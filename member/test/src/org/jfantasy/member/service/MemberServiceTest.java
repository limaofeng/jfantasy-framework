package org.jfantasy.member.service;

import org.jfantasy.framework.service.PasswordTokenType;
import org.jfantasy.member.MemberApplication;
import org.jfantasy.member.bean.Member;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MemberApplication.class)
@ActiveProfiles("dev")
public class MemberServiceTest {

    @Autowired
    private MemberService memberService;

    @Test
    @Transactional
    public void get() throws Exception {
        Member member = memberService.get(1L);
        member.getProfile();
    }

    @Test
    @Transactional
    public void changePassword() throws Exception {
        memberService.changePassword(2181L, PasswordTokenType.macode,"772987","772987");
    }

}