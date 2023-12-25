package net.asany.jfantasy.framework.util.cglib;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.ognl.OgnlUtil;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.junit.jupiter.api.Test;

@Slf4j
public class CglibUtilTest {

  @Test
  public void testNewInstance() throws Exception {
    Object object =
        CglibUtil.newInstance(
            CglibUtilBean.class,
            new MethodInterceptor() {
              @Override
              public Object intercept(
                  Object o, Method method, Object[] objects, MethodProxy methodProxy)
                  throws Throwable {
                return methodProxy.invokeSuper(o, objects);
              }
            });
    log.debug(object.toString());
    OgnlUtil.getInstance().setValue("integer", object, "123456");
    OgnlUtil.getInstance().setValue("string", object, "limaofeng");
  }

  @Test
  public void testGetDefaultInterceptor() throws Exception {
    CglibUtil.getDefaultInterceptor("validator");
  }
}
