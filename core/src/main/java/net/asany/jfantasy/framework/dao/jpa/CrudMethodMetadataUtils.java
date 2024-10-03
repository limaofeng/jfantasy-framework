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
package net.asany.jfantasy.framework.dao.jpa;

import java.lang.reflect.Method;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.data.jpa.repository.support.CrudMethodMetadata;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;

/**
 * 自定义 JpaRepository 时，获取 CrudMethodMetadata 对象
 *
 * @author limaofeng
 */
public class CrudMethodMetadataUtils {

  private static final ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

  public static CrudMethodMetadata getCrudMethodMetadata() {
    ProxyFactory factory = new ProxyFactory();
    factory.addInterface(CrudMethodMetadata.class);
    factory.setTargetSource(new CrudMethodMetadataUtils.ThreadBoundTargetSource());
    return (CrudMethodMetadata) factory.getProxy(classLoader);
  }

  private static class ThreadBoundTargetSource implements TargetSource {

    private static final Method currentInvocation;

    static {
      Class<?> clazz =
          ClassUtil.forName(
              "org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor$CrudMethodMetadataPopulatingMethodInterceptor");
      assert clazz != null;
      currentInvocation = ClassUtil.getDeclaredMethod(clazz, "currentInvocation");
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.aop.TargetSource#getTargetClass()
     */
    @Override
    public Class<?> getTargetClass() {
      return CrudMethodMetadata.class;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.aop.TargetSource#isStatic()
     */
    @Override
    public boolean isStatic() {
      return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.aop.TargetSource#getTarget()
     */
    @Override
    public Object getTarget() {
      MethodInvocation invocation = ClassUtil.invoke(currentInvocation);
      return TransactionSynchronizationManager.getResource(invocation.getMethod());
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.aop.TargetSource#releaseTarget(java.lang.Object)
     */
    @Override
    public void releaseTarget(@NotNull Object target) {}
  }
}
