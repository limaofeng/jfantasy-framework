package org.jfantasy.sns.event;

import org.jfantasy.sns.bean.Snser;
import org.springframework.context.ApplicationEvent;

public class BindSnserEvent extends ApplicationEvent {

    public BindSnserEvent(Snser source) {
        super(source);
    }

    public Snser getSnser(){
        return (Snser)this.getSource();
    }
}
