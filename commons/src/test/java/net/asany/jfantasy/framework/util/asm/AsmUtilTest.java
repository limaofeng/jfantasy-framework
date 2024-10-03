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
package net.asany.jfantasy.framework.util.asm;

import static org.objectweb.asm.Opcodes.IADD;
import static org.objectweb.asm.Opcodes.ILOAD;
import static org.objectweb.asm.Opcodes.IRETURN;

import java.lang.reflect.InvocationTargetException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

@Slf4j
class AsmUtilTest {

  @Test
  void makeClass() {
    Class newClass =
        AsmUtil.makeClass(
            "net.asany.jfantasy.framework.util.asm.DemoBean",
            Property.builder()
                .name("name")
                .descriptor("Ljava/util/List;")
                .signature("Ljava/util/List<Lorg/jfantasy/framework/util/asm/DemoBean;>;")
                .build(),
            Property.builder().type(boolean.class).name("flag").build(),
            Property.builder().type(String.class).name("sex").build());
    log.debug("newClass" + newClass.getName());
  }

  @Test
  void makeEnum() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Class<?> newClass =
        AsmUtil.makeEnum(
            "cn.asany.test.CnAsanyTestAccountOrderBy",
            "createdAt_ASC",
            "createdAt_DESC",
            "updatedAt_ASC",
            "updatedAt_DESC",
            "name_ASC",
            "name_DESC");
    log.debug("newClass" + newClass.getName());
    newClass.getMethod("values").invoke(null);
    @SuppressWarnings({"unchecked", "rawtypes"})
    Object eObj = Enum.valueOf((Class) newClass, "updatedAt_DESC");
  }

  @Test
  void makeClassWithMethods() {
    Class newClass =
        AsmUtil.makeClass(
            "net.asany.jfantasy.framework.util.asm.DemoBean",
            Object.class.getName(),
            MethodInfo.builder()
                .methodName("add")
                .methodDescriptor("(II)I")
                .methodCreator(
                    (mv) -> {
                      mv.visitCode();
                      mv.visitVarInsn(ILOAD, 1);
                      mv.visitVarInsn(ILOAD, 2);
                      mv.visitInsn(IADD);
                      mv.visitInsn(IRETURN);
                      mv.visitMaxs(2, 3);
                    })
                .build());
    log.debug("newClass" + newClass.getName());
  }
}
