package net.asany.jfantasy.framework.dao.mybatis.keygen.util;

import net.asany.jfantasy.framework.dao.mybatis.keygen.service.SequenceService;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 数据库序列生成器
 *
 * @author limaofeng
 */
public class DataBaseKeyGenerator {

  private static DataBaseKeyGenerator dataBaseKeyGenerator;

  private final int poolSize;

  @Autowired SequenceService sequenceService;

  public DataBaseKeyGenerator() {
    this.poolSize = 10;
  }

  public DataBaseKeyGenerator(int poolSize) {
    this.poolSize = poolSize;
  }

  public static DataBaseKeyGenerator getInstance() {
    if (ObjectUtil.isNull(dataBaseKeyGenerator)) {
      dataBaseKeyGenerator = SpringBeanUtils.getBeanByType(DataBaseKeyGenerator.class);
    }
    return dataBaseKeyGenerator;
  }

  public long nextValue(String key) {
    return DatabaseSequenceGenerator.nextValue(key);
  }

  public void reset(String key) {
    DatabaseSequenceGenerator.keys.remove(key);
    this.sequenceService.delete(key);
  }
}
