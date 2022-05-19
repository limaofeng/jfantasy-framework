package org.jfantasy.framework.dao.mybatis.proxy;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;

/**
 * MyBatis Mapper 方法
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:39:09
 */
@Slf4j
public class MyBatisMapperMethod {
  private final SqlSession sqlSession;
  private final Configuration config;
  private String commandName;
  private final Class<?> declaringInterface;
  private final Method method;
  private Integer pageIndex;
  private final List<String> paramNames;
  private final List<Integer> paramPositions;
  private boolean hasNamedParameters;

  /**
   * @param declaringInterface Mapper接口
   * @param method java.lang.reflect.Method;对象
   * @param sqlSession SqlSession
   */
  public MyBatisMapperMethod(Class<?> declaringInterface, Method method, SqlSession sqlSession) {
    this.paramNames = new ArrayList<>();
    this.paramPositions = new ArrayList<>();
    this.sqlSession = sqlSession;
    this.method = method;
    this.config = sqlSession.getConfiguration();
    this.hasNamedParameters = false;
    this.declaringInterface = declaringInterface;
    setupFields();
    setupMethodSignature();
    setupCommandType();
    setupResultMap();
    validateStatement();
  }

  private void setupResultMap() {
    Map<String, Map<String, ResultMapping>> resultMaps = new HashMap<>();
    MappedStatement ms = this.sqlSession.getConfiguration().getMappedStatement(this.commandName);
    for (ResultMap resultMap : ms.getResultMaps()) {
      if (resultMaps.containsKey(resultMap.getId())) {
        continue;
      }
      Map<String, ResultMapping> mappings = new HashMap<>();
      for (ResultMapping mapping : resultMap.getResultMappings()) {
        if (mappings.containsKey(mapping.getProperty())) {
          continue;
        }
        mappings.put(mapping.getProperty(), mapping);
      }
      resultMaps.put(resultMap.getId(), mappings);
    }
  }

  public Object execute(Object[] args) {
    Map<String, Object> param = getParam(args);
    Pager<Object> pager = (Pager<Object>) param.get("pager");
    pager.reset(this.sqlSession.selectList(this.commandName, param));
    return pager;
  }

  /**
   * 将参数转换为Map<String, Object>
   *
   * @param args 查询参数
   * @return Map
   */
  private Map<String, Object> getParam(Object[] args) {
    Map<String, Object> param = new HashMap<>();
    int paramCount = this.paramPositions.size();
    if (args == null || paramCount == 0) {
      if (args != null) {
        param.put("pager", getPager((Pager<Object>) args[this.pageIndex]));
      }
      return param;
    }
    if ((!this.hasNamedParameters) && (paramCount == 1)) {
      if ((ObjectUtil.isNull(args[this.paramPositions.get(0)]))
          || (ClassUtil.isPrimitiveWrapper(args[this.paramPositions.get(0)].getClass()))) {
        param.put("value", args[this.paramPositions.get(0)]);
      } else {
        param.putAll(ObjectUtil.toMap(args[this.paramPositions.get(0)]));
      }
      param.put("pager", getPager((Pager<Object>) args[this.pageIndex]));
      return param;
    }
    for (int i = 0; i < paramCount; i++) {
      param.put(this.paramNames.get(i), args[this.paramPositions.get(i)]);
    }
    param.put("pager", getPager((Pager<Object>) args[this.pageIndex]));
    return param;
  }

  /**
   * 检查Pager对象是否为空，为空初始化一个新的Pager对象
   *
   * @param page Pager<Object>
   * @return Pager<Object>
   */
  private Pager<Object> getPager(Pager<Object> page) {
    if (ObjectUtil.isNull(page)) {
      page = new Pager<>();
    }
    return page;
  }

  /** 获取方法的名称和参数下标 */
  private void setupMethodSignature() {
    Class<?>[] argTypes = this.method.getParameterTypes();
    for (int i = 0; i < argTypes.length; i++) {
      if (Pager.class.isAssignableFrom(argTypes[i])) {
        this.pageIndex = i;
      } else {
        String paramName = String.valueOf(this.paramPositions.size());
        paramName = getParamNameFromAnnotation(i, paramName);
        this.paramNames.add(paramName);
        this.paramPositions.add(i);
      }
    }
  }

  /**
   * 获取Param注解的名称
   *
   * @param i 位置下标
   * @param paramName 参数名称
   * @return 注解名称
   */
  private String getParamNameFromAnnotation(int i, String paramName) {
    Object[] paramAnnos = this.method.getParameterAnnotations()[i];
    for (Object paramAnno : paramAnnos) {
      if (paramAnno instanceof Param) {
        this.hasNamedParameters = true;
        paramName = ((Param) paramAnno).value();
      }
    }
    return paramName;
  }

  /** 获取方法对应 sql Mapper 中的 command Name */
  private void setupFields() {
    this.commandName = (this.declaringInterface.getName() + "." + this.method.getName());
  }

  /** 验证 CommandType 是否为Select查询 */
  private void setupCommandType() {
    MappedStatement ms = this.config.getMappedStatement(this.commandName);
    SqlCommandType type = ms.getSqlCommandType();
    if (type != SqlCommandType.SELECT) {
      throw new BindingException("Unsupport execution method for: " + this.commandName);
    }
  }

  private void validateStatement() {
    if (!this.config.hasStatement(this.commandName)) {
      throw new BindingException("Invalid bound statement (not found): " + this.commandName);
    }
  }
}
