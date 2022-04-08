package org.jfantasy.framework.dao.mybatis.keygen.util;

import org.jfantasy.framework.dao.mybatis.keygen.service.SequenceService;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.springframework.beans.factory.annotation.Autowired;

/** @author limaofeng */
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
    return SequenceInfo.retrieve(this.sequenceService, this.poolSize, key).nextValue();
  }
}
