package org.jfantasy.framework.dao.mybatis;

public class MyBatisException extends RuntimeException {

    public MyBatisException(String message) {
        super(message);
    }

    public MyBatisException(String message, Exception e) {
        super(message, e);
    }
}
