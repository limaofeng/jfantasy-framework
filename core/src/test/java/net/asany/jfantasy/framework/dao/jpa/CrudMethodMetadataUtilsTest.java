package net.asany.jfantasy.framework.dao.jpa;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Method;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;

class CrudMethodMetadataUtilsTest {

  @Test
  void getCrudMethodMetadata() {
    Class<?> clazz =
        ClassUtil.forName(
            "org.springframework.data.jpa.repository.support.CrudMethodMetadataPostProcessor$CrudMethodMetadataPopulatingMethodInterceptor");
    assert clazz != null;
    Method method = ClassUtil.getDeclaredMethod(clazz, "currentInvocation");
    MethodInvocation invocation = ClassUtil.invoke(method, null);
    invocation.getMethod();
  }
}
