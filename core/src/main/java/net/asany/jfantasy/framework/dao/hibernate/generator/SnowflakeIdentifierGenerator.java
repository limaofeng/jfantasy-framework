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
package net.asany.jfantasy.framework.dao.hibernate.generator;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.util.Properties;
import net.asany.jfantasy.framework.dao.hibernate.annotations.SnowflakeFormat;
import net.asany.jfantasy.framework.dao.hibernate.annotations.SnowflakeGenerator;
import net.asany.jfantasy.framework.util.common.PaddingType;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.factory.spi.CustomIdGeneratorCreationContext;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

public class SnowflakeIdentifierGenerator implements IdentifierGenerator {

  private SnowflakeFormat format;
  private long length;
  private PaddingType paddingType;
  private Snowflake snowflake;
  private boolean toStr = false;

  public SnowflakeIdentifierGenerator() {}

  public SnowflakeIdentifierGenerator(
      SnowflakeGenerator snowflakeGenerator,
      Member member,
      CustomIdGeneratorCreationContext context) {
    this();
    long workerId = snowflakeGenerator.workerId();
    long dataCenterId = snowflakeGenerator.dataCenterId();
    this.format = snowflakeGenerator.format();
    this.length = snowflakeGenerator.length();
    this.paddingType = snowflakeGenerator.paddingType();
    this.snowflake = IdUtil.getSnowflake(workerId, dataCenterId);
    this.toStr = ((Field) member).getType() == String.class;
  }

  @Override
  public void configure(Type type, Properties params, ServiceRegistry serviceRegistry)
      throws MappingException {
    IdentifierGenerator.super.configure(type, params, serviceRegistry);
    String workerId = params.getProperty("workerId", "1");
    String dataCenterId = params.getProperty("dataCenterId", "1");
    this.snowflake = IdUtil.getSnowflake(Long.parseLong(workerId), Long.parseLong(dataCenterId));
    this.format = SnowflakeFormat.valueOf(params.getProperty("format", "BASE62"));
    this.length = Long.parseLong(params.getProperty("length", "0"));
    this.paddingType = PaddingType.valueOf(params.getProperty("paddingType", "PREFIX"));
    this.toStr = type.getReturnedClass() == String.class;
  }

  @Override
  public Serializable generate(SharedSessionContractImplementor session, Object object)
      throws HibernateException {
    if (this.toStr) {
      return switch (format) {
        case BASE62 -> {
          if (length == 0) {
            yield StringUtil.base62(snowflake.nextId());
          }
          yield StringUtil.append(
              StringUtil.base62(snowflake.nextId()),
              (int) length,
              paddingType,
              StringUtil.BASE62_CHARS);
        }
        case NONE -> snowflake.nextIdStr();
      };
    }
    return snowflake.nextId();
  }
}
