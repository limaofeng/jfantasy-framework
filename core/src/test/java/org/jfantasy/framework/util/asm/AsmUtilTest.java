package org.jfantasy.framework.util.asm;

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
            "org.jfantasy.framework.util.asm.DemoBean",
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
    Class newClass =
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
    Object eObj = Enum.valueOf(newClass, "updatedAt_DESC");
  }

  @Test
  void makeClassWithMethods() {
    Class newClass =
        AsmUtil.makeClass(
            "org.jfantasy.framework.util.asm.DemoBean",
            "graphql.kickstart.tools.GraphQLQueryResolver",
            new MethodInfo(
                "add",
                "(II)I",
                null,
                (mv) -> {
                  mv.visitCode();
                  mv.visitVarInsn(ILOAD, 1);
                  mv.visitVarInsn(ILOAD, 2);
                  mv.visitInsn(IADD);
                  mv.visitInsn(IRETURN);
                  mv.visitMaxs(2, 3);
                }));
    log.debug("newClass" + newClass.getName());
  }
}
