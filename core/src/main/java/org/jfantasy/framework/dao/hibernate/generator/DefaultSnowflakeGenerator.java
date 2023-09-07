package org.jfantasy.framework.dao.hibernate.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import java.io.Serializable;
import java.lang.reflect.Member;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.mapping.RootClass;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.jfantasy.framework.dao.hibernate.annotations.SnowflakeGenerator;

public class DefaultSnowflakeGenerator implements IdentifierGenerator {

  private Snowflake snowflake;

  private boolean toStr = false;

  public DefaultSnowflakeGenerator() {}

  public DefaultSnowflakeGenerator(
      SnowflakeGenerator snowflakeGenerator,
      Member member,
      CustomIdGeneratorCreationContext generatorCreationContext) {
    this();
    RootClass rootClass = generatorCreationContext.getRootClass();

    long workerId = snowflakeGenerator.workerId();
    long dataCenterId = snowflakeGenerator.dataCenterId();
    this.snowflake = IdUtil.getSnowflake(workerId, dataCenterId);

    toStr = member.getDeclaringClass() == String.class;
  }

  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
      throws MappingException {
    IdentifierGenerator.super.configure(type, params, serviceRegistry);
    String workerId = params.getProperty("workerId", "1");
    String dataCenterId = params.getProperty("dataCenterId", "1");
    this.snowflake = IdUtil.getSnowflake(Long.parseLong(workerId), Long.parseLong(dataCenterId));
    // TODO: SpringBoot 升级遗留问题
    //    this.toStr = type == StringType.INSTANCE;
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    return this.toStr ? snowflake.nextIdStr() : snowflake.nextId();
  }
}
