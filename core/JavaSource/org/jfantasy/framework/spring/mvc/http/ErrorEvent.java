package org.jfantasy.framework.spring.mvc.http;

import org.springframework.context.ApplicationEvent;

public class ErrorEvent extends ApplicationEvent {

    public ErrorEvent(ErrorResponse error) {
        super(error);
    }

}
