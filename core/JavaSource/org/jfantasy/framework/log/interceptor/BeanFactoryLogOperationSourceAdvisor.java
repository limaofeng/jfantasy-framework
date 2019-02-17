package org.jfantasy.framework.log.interceptor;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

import org.jfantasy.framework.log.annotation.LogOperationSource;

public class BeanFactoryLogOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

    private transient LogOperationSource logOperationSource;

    private final LogOperationSourcePointcut pointcut = new LogOperationSourcePointcut() {
        @Override
        protected LogOperationSource getLogOperationSource() {
            return logOperationSource;
        }
    };

    @Override
    public Pointcut getPointcut() {
        return pointcut;
    }

    public void setLogOperationSource(LogOperationSource logOperationSource) {
        this.logOperationSource = logOperationSource;
    }

    public void setClassFilter(ClassFilter classFilter) {
        this.pointcut.setClassFilter(classFilter);
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
