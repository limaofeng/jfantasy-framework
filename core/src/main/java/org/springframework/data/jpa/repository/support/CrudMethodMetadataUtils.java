package org.springframework.data.jpa.repository.support;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.ClassUtils;

/**
 * 自定义 JpaRepository 时，获取 CrudMethodMetadata 对象
 * @author limaofeng
 */
public class CrudMethodMetadataUtils {

    private static ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

    public static CrudMethodMetadata getCrudMethodMetadata() {
        ProxyFactory factory = new ProxyFactory();
        factory.addInterface(CrudMethodMetadata.class);
        factory.setTargetSource(new CrudMethodMetadataUtils.ThreadBoundTargetSource());
        return (CrudMethodMetadata) factory.getProxy(classLoader);
    }

    private static class ThreadBoundTargetSource implements TargetSource {

        /*
         * (non-Javadoc)
         * @see org.springframework.aop.TargetSource#getTargetClass()
         */
        @Override
        public Class<?> getTargetClass() {
            return CrudMethodMetadata.class;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.aop.TargetSource#isStatic()
         */
        @Override
        public boolean isStatic() {
            return false;
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.aop.TargetSource#getTarget()
         */
        @Override
        public Object getTarget() {

            MethodInvocation invocation = CrudMethodMetadataPostProcessor.CrudMethodMetadataPopulatingMethodInterceptor.currentInvocation();
            return TransactionSynchronizationManager.getResource(invocation.getMethod());
        }

        /*
         * (non-Javadoc)
         * @see org.springframework.aop.TargetSource#releaseTarget(java.lang.Object)
         */
        @Override
        public void releaseTarget(Object target) {}
    }
}
