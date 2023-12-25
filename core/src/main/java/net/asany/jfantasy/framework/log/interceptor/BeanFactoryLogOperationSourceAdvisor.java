package net.asany.jfantasy.framework.log.interceptor;

import lombok.Setter;
import net.asany.jfantasy.framework.log.annotation.LogOperationSource;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class BeanFactoryLogOperationSourceAdvisor extends AbstractBeanFactoryPointcutAdvisor {

  @Setter private transient LogOperationSource logOperationSource;

  private final LogOperationSourcePointcut pointcut =
      new LogOperationSourcePointcut() {
        @Override
        protected LogOperationSource getLogOperationSource() {
          return logOperationSource;
        }
      };

  @Override
  public @NotNull Pointcut getPointcut() {
    return pointcut;
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
