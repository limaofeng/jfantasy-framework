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
