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
package net.asany.jfantasy.framework.dao.mybatis.keygen;

import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.spring.SpringBeanUtils;
import net.asany.jfantasy.framework.util.common.StringUtil;
import ognl.Ognl;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

/**
 * GUID序列生成器
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-1-14 下午02:09:32
 */
@Slf4j
public class GUIDKeyGenerator implements KeyGenerator {
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
      log.error("自动设置ID失败", e);
    }
  }

  @Override
  public void processAfter(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {}
}
