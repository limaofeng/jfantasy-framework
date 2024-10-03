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
