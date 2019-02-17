package org.jfantasy.framework.spring.mvc.error;

import org.junit.Test;


public class ValidationExceptionTest {

    @Test
    public void getCode() throws Exception {
        ValidationException exception = new ValidationException(501010,"");
        System.out.println(exception.getCode());
    }

}