package org.jfantasy.member.listener;

import org.jfantasy.framework.jackson.JSON;
import org.jfantasy.member.event.MemberTargetBindEvent;
import org.jfantasy.member.event.MemberTargetBindSource;
import org.jfantasy.ms.EventEmitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 将事件发布到MNS
 */
@Component
public class PublishMemberTargetBindEventByMNS implements ApplicationListener<MemberTargetBindEvent> {

    private EventEmitter eventEmitter;

    @Override
    public void onApplicationEvent(MemberTargetBindEvent event) {
        MemberTargetBindSource source = event.getMemberTarget();
        eventEmitter.fireEvent("mtarget.bind", source.getMemberId().toString(), String.format("用户[%s]绑定[%s]=>[%s]", source.getMemberId(), source.getType(), source.getTarget()), JSON.serialize(source));
    }

    @Autowired
    public void setEventEmitter(EventEmitter eventEmitter) {
        this.eventEmitter = eventEmitter;
    }
}
