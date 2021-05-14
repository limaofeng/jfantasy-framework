package org.jfantasy.framework.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ErrorUtilsTest {

    @Test
    void errorCode() {
        System.out.println(ErrorUtils.errorCode(new ValidationException("xxxx")));

    }
}