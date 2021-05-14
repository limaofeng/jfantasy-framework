package org.jfantasy.framework.security.core;

import org.springframework.context.support.MessageSourceAccessor;

/**
 * @author limaofeng
 */
public class SecurityMessageSource {

    private static MessageSourceAccessor accessor;

    public static MessageSourceAccessor getAccessor() {
        return SecurityMessageSource.accessor;
    }

    public static void setAccessor(MessageSourceAccessor accessor) {
        SecurityMessageSource.accessor = accessor;
    }

}
