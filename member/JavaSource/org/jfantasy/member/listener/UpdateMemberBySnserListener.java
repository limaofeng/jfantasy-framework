package org.jfantasy.member.listener;

import org.jfantasy.member.service.MemberService;
import org.jfantasy.sns.event.BindSnserEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class UpdateMemberBySnserListener implements ApplicationListener<BindSnserEvent> {

    private final MemberService memberService;

    @Autowired
    public UpdateMemberBySnserListener(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    @Async
    public void onApplicationEvent(BindSnserEvent event) {
        memberService.update(event.getSnser());
    }

}
