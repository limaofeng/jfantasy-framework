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

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import java.sql.Statement;
import lombok.extern.slf4j.Slf4j;
import ognl.Ognl;
import ognl.OgnlException;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;

@Slf4j
public class SnowflakeKeyGenerator implements KeyGenerator {

  private static final Snowflake SNOWFLAKE = IdUtil.getSnowflake(1, 1);

  @Override
  public void processBefore(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {
    String[] keyProperties = paramMappedStatement.getKeyProperties();
    if (keyProperties.length == 1) {
      try {
        Ognl.setValue(keyProperties[0], paramObject, nextValue(paramObject.getClass().getName()));
      } catch (OgnlException e) {
        log.error(e.getMessage(), e);
      }
    }
  }

  private Object nextValue(String name) {
    return SNOWFLAKE.nextId();
  }

  @Override
  public void processAfter(
      Executor paramExecutor,
      MappedStatement paramMappedStatement,
      Statement paramStatement,
      Object paramObject) {
    processBefore(paramExecutor, paramMappedStatement, paramStatement, paramObject);
  }
}
