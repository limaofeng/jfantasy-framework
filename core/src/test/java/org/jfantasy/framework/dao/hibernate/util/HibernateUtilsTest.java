package org.jfantasy.framework.dao.hibernate.util;

import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.dao.mybatis.keygen.bean.Sequence;
import org.junit.jupiter.api.Test;

@Slf4j
class HibernateUtilsTest {

  @Test
  public void getEntityName() throws Exception {
    String name = HibernateUtils.getEntityName(Sequence.class);
    log.debug(name);
  }

  @Test
  public void getTableName() throws Exception {
    String name = HibernateUtils.getTableName(Sequence.class);
    log.debug(name);
  }

  @Test
  void getIdName() {
    String name = HibernateUtils.getIdName(Sequence.class);
    log.debug(name);
  }

  @Test
  void getIdValue() {
    Sequence sequence = new Sequence();
    sequence.setKey("1123");
    String value = HibernateUtils.getIdValue(Sequence.class, sequence);
    log.debug(value);
  }
}
