package net.asany.jfantasy.framework.dao.mybatis.proxy;

import java.io.Serial;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.Page;
import net.asany.jfantasy.framework.error.IgnoreException;
import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.session.SqlSession;
import org.springframework.util.ReflectionUtils;

/**
 * MyBatis Mapper 代理
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2012-10-28 下午08:33:14
 */
@Slf4j
public class MyBatisMapperProxy implements InvocationHandler {
  private final SqlSession sqlSession;

  /** 不需要代理的方法 */
  private static final Set<String> OBJECT_METHODS =
      new HashSet<>() {
        @Serial private static final long serialVersionUID = -1782950882770203583L;

        {
          add("toString");
          add("getClass");
          add("hashCode");
          add("equals");
          add("wait");
          add("notify");
          add("notifyAll");
        }
      };

  private boolean isObjectMethod(Method method) {
    return OBJECT_METHODS.contains(method.getName());
  }

  private MyBatisMapperProxy(SqlSession sqlSession) {
    this.sqlSession = sqlSession;
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (isObjectMethod(method)) {
      return null;
    }
    final Class<?> declaringInterface = findDeclaringInterface(proxy, method);
    if (Page.class.isAssignableFrom(method.getReturnType())) {
      return new MyBatisMapperMethod(
              getDeclaringInterface(declaringInterface, method), method, this.sqlSession)
          .execute(args);
    }
    MapperMethod mapperMethod =
        new MapperMethod(
            getDeclaringInterface(declaringInterface, method),
            method,
            this.sqlSession.getConfiguration());
    Object result = mapperMethod.execute(this.sqlSession, args);
    if ((result == null) && (method.getReturnType().isPrimitive())) {
      throw new BindingException(
          "Mapper method '"
              + method.getName()
              + "' ("
              + method.getDeclaringClass()
              + ") attempted to return null from a method with a primitive return type ("
              + method.getReturnType()
              + ").");
    }
    return result;
  }

  private Class<?> getDeclaringInterface(Class<?> declaringInterface, Method method) {
    if (!this.sqlSession
        .getConfiguration()
        .hasStatement(declaringInterface.getName() + "." + method.getName())) {
      Class<?>[] superInterfaces = declaringInterface.getInterfaces();
      for (Class<?> interfaceClass : superInterfaces) {
        log.debug("向父接口查找Mapper:" + interfaceClass.getName() + "." + method.getName());
        if (this.sqlSession
            .getConfiguration()
            .hasStatement(interfaceClass.getName() + "." + method.getName())) {
          return interfaceClass;
        }
      }
      throw new IgnoreException(declaringInterface.getName() + "." + method.getName() + "未正确配置!");
    }
    return declaringInterface;
  }

  private Class<?> findDeclaringInterface(Object proxy, Method method) {
    Class<?> declaringInterface = null;
    for (Class<?> interfaceClass : proxy.getClass().getInterfaces()) {
      Method m =
          ReflectionUtils.findMethod(interfaceClass, method.getName(), method.getParameterTypes());
      if (m != null) {
        declaringInterface = interfaceClass;
      }
    }
    if (declaringInterface == null) {
      throw new BindingException("Could not find interface with the given method " + method);
    }
    return declaringInterface;
  }

  /**
   * 生成代理方法
   *
   * @param <T> 泛型
   * @param mapperInterface mapper接口
   * @param sqlSession sqlSession
   * @return T
   */
  public static <T> T newMapperProxy(Class<T> mapperInterface, SqlSession sqlSession) {
    ClassLoader classLoader = mapperInterface.getClassLoader();
    Class<?>[] interfaces = {mapperInterface};
    MyBatisMapperProxy proxy = new MyBatisMapperProxy(sqlSession);
    //noinspection unchecked
    return (T) Proxy.newProxyInstance(classLoader, interfaces, proxy);
  }
}
