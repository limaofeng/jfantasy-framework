package org.jfantasy.framework.dao;

import java.sql.Connection;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Lazy;

@Aspect
@Lazy(false)
public class DataSourceSetUtf8mb4 {

  @Around("execution(public * javax.sql.DataSource.getConnection(..))")
  public Connection getConnection(ProceedingJoinPoint pjp) throws Throwable {
    Connection connection = (Connection) pjp.proceed();
    connection.prepareStatement("set names utf8mb4").executeQuery(); // NOSONAR
    return connection;
  }
}
