package net.asany.jfantasy.framework.dao.mybatis.interceptors;

import jakarta.persistence.GeneratedValue;
import java.lang.reflect.Field;
import java.util.*;
import net.asany.jfantasy.framework.dao.mybatis.keygen.GUIDKeyGenerator;
import net.asany.jfantasy.framework.dao.mybatis.keygen.MultiKeyGenerator;
import net.asany.jfantasy.framework.dao.mybatis.keygen.SequenceKeyGenerator;
import net.asany.jfantasy.framework.dao.mybatis.keygen.SnowflakeKeyGenerator;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import org.apache.ibatis.executor.keygen.KeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;

/**
 * 注解序列拦截器(扩展mybatis注解主键生成)
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:13:36
 */
@Intercepts({
  @org.apache.ibatis.plugin.Signature(
      type = org.apache.ibatis.executor.Executor.class,
      method = "update",
      args = {MappedStatement.class, Object.class})
})
public class AutoKeyInterceptor implements Interceptor {

  static int MAPPED_STATEMENT_INDEX = 0;
  static int PARAMETER_INDEX = 1;

  private Map<String, KeyGenerator> keyGenerators = null;

  public void initKeyGenerators() {
    if (ObjectUtil.isNull(keyGenerators)) {
      keyGenerators = new HashMap<>();
    }
    keyGenerators.put("system-uuid", GUIDKeyGenerator.getInstance());
    keyGenerators.put("fantasy-sequence", new SequenceKeyGenerator());
    keyGenerators.put("snowflake", new SnowflakeKeyGenerator());
  }

  @Override
  public Object intercept(Invocation invocation) throws Throwable {
    Object[] queryArgs = invocation.getArgs();
    MappedStatement ms = (MappedStatement) queryArgs[MAPPED_STATEMENT_INDEX];
    if (SqlCommandType.INSERT.equals(ms.getSqlCommandType())) {
      bindKeyGenerator(ms);
    }
    return invocation.proceed();
  }

  private void bindKeyGenerator(MappedStatement ms) {
    String[] keyProperties = ms.getKeyProperties();
    if ((ObjectUtil.isNull(keyProperties))
        && (ObjectUtil.isNotNull(ms.getParameterMap().getType()))) {
      Field[] fields =
          ClassUtil.getDeclaredFields(ms.getParameterMap().getType(), GeneratedValue.class);
      List<String> keyPropertieNameList = new ArrayList<>();
      Map<String, KeyGenerator> targetKeyGenerators = new HashMap<>();
      for (Field field : fields) {
        targetKeyGenerators.put(
            field.getName(),
            this.getKeyGenerator(
                ClassUtil.getFieldGenericType(field, GeneratedValue.class).generator()));
        if (ObjectUtil.isNull(field.getName())) {
          targetKeyGenerators.remove(field.getName());
        } else {
          keyPropertieNameList.add(field.getName());
        }
      }
      if (!targetKeyGenerators.isEmpty()) {
        ClassUtil.setValue(
            ms, "keyProperties", keyPropertieNameList.toArray(new String[fields.length]));
        ClassUtil.setValue(ms, "keyGenerator", new MultiKeyGenerator(targetKeyGenerators));
      }
    }
  }

  @Override
  public Object plugin(Object target) {
    return Plugin.wrap(target, this);
  }

  @Override
  public void setProperties(Properties properties) {}

  private KeyGenerator getKeyGenerator(String key) {
    if (this.keyGenerators == null) {
      this.initKeyGenerators();
    }
    return this.keyGenerators.get(key);
  }

  public void setKeyGenerators(Map<String, KeyGenerator> keyGenerators) {
    this.keyGenerators = keyGenerators;
  }
}
