package org.jfantasy.framework.dao.mybatis.keygen;

import java.sql.Statement;
import ognl.Ognl;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.StringUtil;

/**
 * GUID序列生成器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:09:32
 */
public class GUIDKeyGenerator implements KeyGenerator {
  private static final Logger LOGGER = LogManager.getLogger(GUIDKeyGenerator.class);
  private static GUIDKeyGenerator instance;

  public static synchronized GUIDKeyGenerator getInstance() {
    if (instance == null && SpringBeanUtils.containsBean(GUIDKeyGenerator.class)) {
      instance = SpringBeanUtils.getBeanByType(GUIDKeyGenerator.class);
    }
    if (instance == null) {
      instance = new GUIDKeyGenerator();
    }
    return instance;
  }

  private GUIDKeyGenerator() {}

  public String getGUID() {
    return StringUtil.guid();
  }

  @Override
  public void processBefore(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {
    String[] keyProperties = paramMappedStatement.getKeyProperties();
    try {
      Ognl.setValue(keyProperties[0], paramObject, getGUID());
    } catch (Exception e) {
      LOGGER.error("自动设置ID失败", e);
    }
  }

  @Override
  public void processAfter(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {}
}
