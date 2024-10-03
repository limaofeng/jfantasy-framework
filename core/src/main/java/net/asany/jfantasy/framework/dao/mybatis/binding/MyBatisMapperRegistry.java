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
package net.asany.jfantasy.framework.dao.mybatis.binding;

import net.asany.jfantasy.framework.dao.mybatis.proxy.MyBatisMapperProxy;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;

/**
 * MyBatis Mapper 登记处
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:30:16
 */
public class MyBatisMapperRegistry extends MapperRegistry {

  public MyBatisMapperRegistry(Configuration config) {
    super(config);
  }

  @Override
  public <T> T getMapper(Class<T> type, SqlSession sqlSession) {
    if (!hasMapper(type)) {
      throw new BindingException("Type " + type + " is not known to the MapperRegistry.");
    }
    try {
      return MyBatisMapperProxy.newMapperProxy(type, sqlSession);
    } catch (Exception e) {
      throw new BindingException("Error getting mapper instance. Cause: " + e, e);
    }
  }
}
