package org.jfantasy.framework.dao.mybatis.keygen;

import java.sql.Statement;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfantasy.framework.dao.mybatis.keygen.util.DataBaseKeyGenerator;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * 序列生成器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:08:52
 */
public class SequenceKeyGenerator implements KeyGenerator {

  private static final Logger LOG = LogManager.getLogger(SequenceKeyGenerator.class);

  private DataBaseKeyGenerator dataBaseKeyGenerator;

  @Override
  public void processBefore(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {
    String[] keyProperties = paramMappedStatement.getKeyProperties();
    if (keyProperties.length == 1) {
      try {
        Ognl.setValue(
            keyProperties[0],
            paramObject,
            getKeyGenerator().nextValue(paramObject.getClass().getName()));
      } catch (OgnlException e) {
        LOG.error(e.getMessage(), e);
      }
    }
  }

  private DataBaseKeyGenerator getKeyGenerator() {
    if (ObjectUtil.isNull(this.dataBaseKeyGenerator)) {
      this.dataBaseKeyGenerator = SpringBeanUtils.getBeanByType(DataBaseKeyGenerator.class);
    }
    return this.dataBaseKeyGenerator;
  }

  @Override
  public void processAfter(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {}

  public void setDataBaseKeyGenerator(DataBaseKeyGenerator dataBaseKeyGenerator) {
    this.dataBaseKeyGenerator = dataBaseKeyGenerator;
  }
}
