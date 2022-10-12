package org.jfantasy.framework.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.objectweb.asm.Opcodes.*;

import lombok.SneakyThrows;
import org.jfantasy.framework.util.asm.AsmUtil;
import org.jfantasy.framework.util.asm.Property;
import org.junit.jupiter.api.Test;

class FantasyClassLoaderTest {

  @Test
  @SneakyThrows
  void loadClass() {
    String classname = "org.jfantasy.framework.util.asm.DemoBean";
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
