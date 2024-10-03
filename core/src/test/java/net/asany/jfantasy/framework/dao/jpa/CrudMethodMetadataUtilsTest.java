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
