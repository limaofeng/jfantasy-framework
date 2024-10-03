/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.dao.mybatis.keygen.service;

import net.asany.jfantasy.framework.dao.mybatis.keygen.bean.Sequence;
import net.asany.jfantasy.framework.dao.mybatis.keygen.dao.SequenceDao;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenceService {

  private final SequenceDao sequenceDao;

  public SequenceService(SequenceDao sequenceDao) {
    this.sequenceDao = sequenceDao;
  }

  /**
   * 判断序列是否存在
   *
   * @param key 序列名称
   * @return boolean
   */
  @Transactional(
      value = "dataSourceTransactionManager",
      rollbackFor = Exception.class,
      propagation = Propagation.REQUIRES_NEW)
  public boolean exists(String key) {
    return ObjectUtil.isNotNull(this.sequenceDao.findUniqueByKey(key));
  }

  @Transactional(value = "dataSourceTransactionManager", readOnly = true)
  public long getCurrentKeyValue(String key) {
    return this.sequenceDao.findUniqueByKey(key).getValue();
  }

  /**
   * 获取序列的下一个值
   *
   * @param key 序列名称
   * @param poolSize 序列增长值
   * @return long
   */
  @Transactional(
      value = "dataSourceTransactionManager",
      rollbackFor = Exception.class,
      propagation = Propagation.REQUIRES_NEW)
  public long next(String key, long poolSize) {
    Sequence sequence = this.sequenceDao.findUniqueByKey(key);
    if (ObjectUtil.isNull(sequence)) {
      return newKey(key, poolSize);
    }
    sequence.setOriginalValue(sequence.getValue());
    sequence.setValue(sequence.getValue() + poolSize);
    int opt = this.sequenceDao.update(sequence);
    if (opt == 0) {
      return next(key, poolSize);
    }
    return sequence.getValue();
  }

  /**
   * 创建一个新的序列
   *
   * @param key 序列名称
   * @param poolSize 序列增长值
   * @return long
   */
  @Transactional(
      value = "dataSourceTransactionManager",
      rollbackFor = Exception.class,
      propagation = Propagation.REQUIRES_NEW)
  public long newKey(String key, long poolSize) {
    String[] keys = RegexpUtil.split(key, ":");
    int index =
        keys.length == 2
            ? ObjectUtil.defaultValue(this.sequenceDao.queryTableSequence(keys[0], keys[1]), 0)
            : 0;
    int opt = this.sequenceDao.insert(new Sequence(key, index + poolSize));
    if (opt == 0) {
      return this.sequenceDao.findUniqueByKey(key).getValue();
    }
    return index + poolSize;
  }

  @Transactional(
      value = "dataSourceTransactionManager",
      rollbackFor = Exception.class,
      propagation = Propagation.REQUIRES_NEW)
  public void delete(String key) {
    this.sequenceDao.delete(key);
  }
}
