package net.asany.jfantasy.framework.util;

import lombok.SneakyThrows;
import net.asany.jfantasy.framework.util.asm.AsmUtil;
import net.asany.jfantasy.framework.util.asm.Property;
import org.junit.jupiter.api.Test;

class FantasyClassLoaderTest {

  @Test
  @SneakyThrows
  void loadClass() {
    String classname = "net.asany.jfantasy.framework.util.asm.DemoBean";
    Class newClass =
        AsmUtil.makeClass(
            classname,
            Object.class.getName(),
            Property.builder().name("name").type(String.class).build());

    Class clazz = Class.forName(classname, false, FantasyClassLoader.getClassLoader());

    assert clazz == newClass;

    newClass =
        AsmUtil.makeClass(
            classname,
            Object.class.getName(),
            Property.builder().name("name").type(String.class).build(),
            Property.builder().name("sex").type(String.class).build());

    clazz = Class.forName(classname, true, FantasyClassLoader.getClassLoader());

    assert clazz == newClass;
  }
}
