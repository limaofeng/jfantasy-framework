package org.jfantasy.member.dao.listener;

import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostInsertEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.member.bean.MemberTarget;
import org.jfantasy.member.event.MemberTargetBindEvent;
import org.springframework.stereotype.Component;

/**
 * 绑定账号
 */
@Component
public class MemberTargetSaveOrUpdateListener extends AbstractChangedListener<MemberTarget> {


    public MemberTargetSaveOrUpdateListener(){
        super(EventType.POST_COMMIT_INSERT);
    }

    @Override
    public void onPostInsert(MemberTarget target, PostInsertEvent event) {
        this.applicationContext.publishEvent(new MemberTargetBindEvent(target.getType().getId(),target.getMember().getId(),target.getValue()));
    }

}
