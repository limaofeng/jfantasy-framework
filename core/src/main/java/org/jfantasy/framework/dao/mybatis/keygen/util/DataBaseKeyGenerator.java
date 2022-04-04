package org.jfantasy.framework.dao.mybatis.keygen.util;

import org.jfantasy.framework.dao.mybatis.keygen.service.SequenceService;
import org.jfantasy.framework.spring.SpringBeanUtils;
import org.jfantasy.framework.util.common.ObjectUtil;

/** @author limaofeng */
public class DataBaseKeyGenerator {

  private static DataBaseKeyGenerator dataBaseKeyGenerator;

  private final int poolSize;

  private final SequenceService sequenceService;

  public DataBaseKeyGenerator(SequenceService sequenceService) {
    this(sequenceService, 10);
  }

  public DataBaseKeyGenerator(SequenceService sequenceService, int poolSize) {
    this.poolSize = poolSize;
    this.sequenceService = sequenceService;
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
