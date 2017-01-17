package org.jfantasy.sns.dao.listener;

import org.hibernate.Hibernate;
import org.hibernate.event.spi.EventType;
import org.hibernate.event.spi.PostDeleteEvent;
import org.hibernate.event.spi.PostInsertEvent;
import org.jfantasy.framework.dao.hibernate.listener.AbstractChangedListener;
import org.jfantasy.sns.bean.Snser;
import org.jfantasy.sns.event.BindSnserEvent;
import org.jfantasy.sns.event.UnBindSnserEvent;
import org.springframework.stereotype.Component;

@Component
public class SnserListener extends AbstractChangedListener<Snser> {

    public SnserListener() {
        super(EventType.POST_COMMIT_INSERT, EventType.POST_COMMIT_DELETE);
    }

    @Override
    protected void onPostInsert(Snser entity, PostInsertEvent event) {
        Hibernate.initialize(entity.getMember());
        this.applicationContext.publishEvent(new BindSnserEvent(entity));
    }

    @Override
    protected void onPostDelete(Snser entity, PostDeleteEvent event) {
        Hibernate.initialize(entity.getMember());
        this.applicationContext.publishEvent(new UnBindSnserEvent(entity));
    }

}
