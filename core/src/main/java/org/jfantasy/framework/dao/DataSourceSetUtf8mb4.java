package org.jfantasy.framework.dao;

import com.mysql.cj.jdbc.DatabaseMetaData;
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
    if (connection.getMetaData() instanceof DatabaseMetaData) {
      connection.prepareStatement("set names utf8mb4").executeQuery();
    }
    return connection;
  }
}
