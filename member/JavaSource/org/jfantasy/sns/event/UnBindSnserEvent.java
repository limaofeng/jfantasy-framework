package org.jfantasy.sns.event;

import org.jfantasy.sns.bean.Snser;
import org.springframework.context.ApplicationEvent;

public class UnBindSnserEvent extends ApplicationEvent {

    public UnBindSnserEvent(Object source) {
        super(source);
    }

    public Snser getSnser(){
        return (Snser)this.getSource();
    }

}
