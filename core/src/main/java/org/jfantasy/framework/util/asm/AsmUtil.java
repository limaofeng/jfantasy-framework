package org.jfantasy.framework.util.asm;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.FantasyClassLoader;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.PathUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.objectweb.asm.*;

@Slf4j
public class AsmUtil implements Opcodes {

  private AsmUtil() {}

  /**
   * 创建一个java动态bean
   *
   * @param className 新生产className
   * @param methods 方法
   * @return 新生成的 class
   */
  public static Class<?> makeClass(String className, MethodInfo[] methods) {
    return makeClass(className, Object.class.getName(), new Class[0], new Property[0], methods);
  }

  public static Class<?> makeClass(String className, Property... properties) {
    return makeClass(
        className, Object.class.getName(), new Class[0], properties, new MethodInfo[0]);
  }

  public static Class<?> makeClass(
      String className, Class<?>[] interfaces, Property... properties) {
    return makeClass(className, Object.class.getName(), interfaces, properties, new MethodInfo[0]);
  }

  /**
   * 创建一个java动态bean
   *
   * @param className 新生产className
   * @param superClassName 父类
   * @param methods 方法
   * @return 新生成的 class
   */
  public static Class<?> makeClass(String className, String superClassName, MethodInfo... methods) {
    return makeClass(className, superClassName, new Class[0], new Property[0], methods);
  }

  public static Class<?> makeClass(
      String className, String superClassName, Property... properties) {
    return makeClass(className, superClassName, new Class[0], properties, new MethodInfo[0]);
  }

  public static Class<?> makeClass(
      String className, String superClassName, Class<?>[] interfaces, Property... properties) {
    return makeClass(className, superClassName, interfaces, properties, new MethodInfo[0]);
  }

  public static Class<?> makeInterface(String className, AnnotationDescriptor descriptor) {
    return makeInterface(className, new AnnotationDescriptor[] {descriptor});
  }

  public static Class<?> makeInterface(
      String className, AnnotationDescriptor descriptor, Class<?> interfacecls) {
    return makeInterface(className, new AnnotationDescriptor[] {descriptor}, interfacecls);
  }

  public static Class<?> makeInterface(
      String className, AnnotationDescriptor[] annotDescs, Class<?>... interfaces) {
    ClassWriter cw = new ClassWriter(F_FULL);

    String newClassInternalName = className.replace('.', '/');

    String[] iters = new String[interfaces.length];
    for (int i = 0, len = interfaces.length; i < len; i++) {
      Class<?> inter = interfaces[i];
      iters[i] = inter.getName().replace('.', '/');
    }
    cw.visit(
        V1_6,
        ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE,
        newClassInternalName,
        null,
        "java/lang/Object",
        iters);

    for (AnnotationDescriptor descriptor : annotDescs) {
      AnnotationVisitor visitor = cw.visitAnnotation(getTypeDescriptor(descriptor.type()), true);
      for (String key : descriptor.keys()) {
        visitor.visit(key, descriptor.valueOf(key));
      }
      visitor.visitEnd();
    }

    cw.visitEnd();

    return loadClass(className, cw.toByteArray());
  }

  public static Class<?> makeEnum(String className, String... values) {
    return makeEnum(
        className,
        Arrays.stream(values)
            .map(v -> EnumValue.builder().name(v).build())
            .toArray(EnumValue[]::new));
  }

  public static Class<?> makeEnum(String className, EnumValue... values) {
    String newClassInternalName = className.replace('.', '/');

    ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    MethodVisitor mv;

    cw.visit(
        V1_5,
        ACC_PUBLIC + ACC_FINAL + ACC_SUPER + ACC_ENUM,
        newClassInternalName,
        "Ljava/lang/Enum<L" + newClassInternalName + ";>;",
        "java/lang/Enum",
        null);

    for (EnumValue enumValue : values) {
      FieldVisitor fv =
          cw.visitField(
              ACC_PUBLIC + ACC_FINAL + ACC_STATIC + ACC_ENUM,
              enumValue.getName(),
              "L" + newClassInternalName + ";",
              null,
              null);
      fv.visitEnd();
    }

    FieldVisitor fv =
        cw.visitField(
            ACC_PRIVATE + ACC_FINAL + ACC_STATIC + ACC_SYNTHETIC,
            "$VALUES",
            "[L" + newClassInternalName + ";",
            null,
            null);
    fv.visitEnd();

    mv =
        cw.visitMethod(
            ACC_PUBLIC + ACC_FINAL + ACC_STATIC,
            "values",
            "()[L" + newClassInternalName + ";",
            null,
            null);
    mv.visitCode();
    mv.visitFieldInsn(
        GETSTATIC, newClassInternalName, "$VALUES", "[L" + newClassInternalName + ";");
    mv.visitMethodInsn(
        INVOKEVIRTUAL, "[L" + newClassInternalName + ";", "clone", "()Ljava/lang/Object;", false);
    mv.visitTypeInsn(CHECKCAST, "[L" + newClassInternalName + ";");
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    mv =
        cw.visitMethod(
            ACC_PUBLIC + ACC_STATIC,
            "valueOf",
            "(Ljava/lang/String;)L" + newClassInternalName + ";",
            null,
            null);
    mv.visitCode();
    mv.visitLdcInsn(Type.getType("L" + newClassInternalName + ";"));
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
        INVOKESTATIC,
        "java/lang/Enum",
        "valueOf",
        "(Ljava/lang/Class;Ljava/lang/String;)Ljava/lang/Enum;",
        false);
    mv.visitTypeInsn(CHECKCAST, newClassInternalName);
    mv.visitInsn(ARETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    mv = cw.visitMethod(ACC_PRIVATE, "<init>", "(Ljava/lang/String;I)V", "()V", null);
    mv.visitCode();
    mv.visitVarInsn(ALOAD, 0);
    mv.visitVarInsn(ALOAD, 1);
    mv.visitVarInsn(ILOAD, 2);
    mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Enum", "<init>", "(Ljava/lang/String;I)V", false);
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    mv = cw.visitMethod(ACC_STATIC, "<clinit>", "()V", null, null);
    mv.visitCode();

    for (int i = 0; i < values.length; i++) {
      EnumValue enumValue = values[i];
      mv.visitTypeInsn(NEW, newClassInternalName);
      mv.visitInsn(DUP);
      mv.visitLdcInsn(enumValue.getName());
      if (ICONST_0 + i > ICONST_5) {
        mv.visitIntInsn(BIPUSH, i);
      } else {
        mv.visitInsn(ICONST_0 + i);
      }
      mv.visitMethodInsn(
          INVOKESPECIAL, newClassInternalName, "<init>", "(Ljava/lang/String;I)V", false);
      mv.visitFieldInsn(
          PUTSTATIC, newClassInternalName, enumValue.getName(), "L" + newClassInternalName + ";");
    }

    if (values.length < 6) {
      mv.visitInsn(ICONST_0 + values.length);
    } else {
      mv.visitIntInsn(BIPUSH, values.length);
    }
    mv.visitTypeInsn(ANEWARRAY, newClassInternalName);

    for (int i = 0; i < values.length; i++) {
      EnumValue enumValue = values[i];
      mv.visitInsn(DUP);
      if (ICONST_0 + i > ICONST_5) {
        mv.visitIntInsn(BIPUSH, i);
      } else {
        mv.visitInsn(ICONST_0 + i);
      }
      mv.visitFieldInsn(
          GETSTATIC, newClassInternalName, enumValue.getName(), "L" + newClassInternalName + ";");
      mv.visitInsn(AASTORE);
    }

    mv.visitFieldInsn(
        PUTSTATIC, newClassInternalName, "$VALUES", "[L" + newClassInternalName + ";");
    mv.visitInsn(RETURN);
    mv.visitMaxs(0, 0);
    mv.visitEnd();

    cw.visitEnd();

    return loadClass(className, cw.toByteArray());
  }

  public static Class<?> makeClass(
      String className,
      String superClassName,
      Class<?>[] interfaces,
      Property[] properties,
      MethodInfo[] methodInfos) {
    return makeClass(className, null, superClassName, interfaces, properties, methodInfos);
  }

  public static Class<?> makeClass(
      String className,
      String signature,
      String superClassName,
      Class<?>[] interfaces,
      Property[] properties,
      MethodInfo[] methodInfos) {
    ClassWriter cw = new ClassWriter(F_FULL);

    String newClassInternalName = className.replace('.', '/');
    String superClassInternalName =
        Type.getInternalName(Objects.requireNonNull(ClassUtil.forName(superClassName)));

    String[] iteRs = new String[interfaces.length];
    for (int i = 0, len = interfaces.length; i < len; i++) {
      Class<?> inter = interfaces[i];
      iteRs[i] = inter.getName().replace('.', '/');
    }

    AsmContext.getContext().set("className", className);
    AsmContext.getContext().set("superClassName", superClassName);

    Label l0 = new Label();
    Label l1 = new Label();

    cw.visit(V1_6, ACC_PUBLIC, newClassInternalName, signature, superClassInternalName, iteRs);

    cw.visitSource(RegexpUtil.parseGroup(className, "\\.([A-Za-z0-9_$]+)$", 1) + ".java", null);

    // 构造方法
    MethodVisitor mv =
        cw.visitMethod(
            ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getReturnType("()V")), null, null);
    mv.visitLabel(l0);
    mv.visitVarInsn(ALOAD, 0);
    mv.visitMethodInsn(
        INVOKESPECIAL,
        superClassInternalName,
        "<init>",
        Type.getMethodDescriptor(Type.getReturnType("()V")),
        false);
    mv.visitInsn(RETURN);
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", getTypeDescriptor(className), null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    for (Property property : properties) {
      makeProperty(cw, property);
    }

    for (MethodInfo methodInfo : methodInfos) {
      makeMethod(
          cw,
          methodInfo.getSymbolTable(),
          methodInfo.getMethodName(),
          methodInfo.getMethodDescriptor(),
          methodInfo.getSignature(),
          methodInfo.getMethodCreator());
    }

    // toString方法
    mv =
        cw.visitMethod(
            ACC_PUBLIC,
            "toString",
            Type.getMethodDescriptor(Type.getType(String.class)),
            null,
            null);
    mv.visitCode();
    mv.visitLabel(l0);
    mv.visitLdcInsn(" AsmUtil makeClass \"" + className + "\" !");
    mv.visitInsn(ARETURN);
    mv.visitLabel(l1);
    mv.visitLocalVariable("this", getTypeDescriptor(className), null, l0, l1, 0);
    mv.visitMaxs(1, 1);
    mv.visitEnd();

    cw.visitEnd();

    return loadClass(className, cw.toByteArray());
  }

  public static <T> Class<T> loadClass(String className, byte[] bytes) {
    try {
      FileUtil.write(
          Paths.get(PathUtil.classes() + "/" + className.replace(".", File.separator) + ".class"),
          bytes);
      //noinspection unchecked
      return FantasyClassLoader.getClassLoader().loadClass(PathUtil.classes(), className);
    } catch (Exception e) {
      log.error(e.getMessage(), e);
      try {
        return FantasyClassLoader.getClassLoader().loadClass(bytes, className);
      } catch (ClassNotFoundException ex) {
        log.error(e.getMessage(), ex);
        throw new IgnoreException(e.getMessage(), ex);
      }
    }
  }

  protected static void makeProperty(ClassWriter classWriter, Property property) {

    String fieldName = property.getName();
    String descriptor = property.getDescriptor();
    String signature = property.getSignature();

    // 属性
    FieldVisitor fieldVisitor =
        classWriter.visitField(ACC_PRIVATE, fieldName, descriptor, signature, null);

    fieldVisitor.visitEnd();

    AsmContext.getContext().set("property", property);

    // set方法
    if (property.isWrite()) {
      makeMethod(
          classWriter,
          ACC_PUBLIC,
          "set" + StringUtil.upperCaseFirst(fieldName),
          Type.getMethodDescriptor(
              Type.getReturnType("()V"), Type.getType(property.getDescriptor())),
          StringUtil.isNotBlank(property.getSignature())
              ? "(" + property.getSignature() + ")V"
              : null,
          property.getSetMethodCreator());
    }

    // get方法
    if (property.isRead()) {
      makeMethod(
          classWriter,
          ACC_PUBLIC,
          (boolean.class == property.getType() ? "is" : "get")
              + StringUtil.upperCaseFirst(fieldName),
          Type.getMethodDescriptor(Type.getType(property.getDescriptor())),
          StringUtil.isNotBlank(property.getSignature()) ? "()" + property.getSignature() : null,
          property.getGetMethodCreator());
    }
  }

  protected static void makeMethod(
      ClassWriter classWriter,
      int symbolTable,
      String methodName,
      String methodDescriptor,
      String signature,
      MethodCreator methodCreator) {
    MethodVisitor mv =
        classWriter.visitMethod(
            symbolTable, methodName, methodDescriptor, signature, new String[] {});

    mv.visitCode();

    methodCreator.execute(mv);

    mv.visitEnd();
  }

  public static int[] loadAndReturnOf(String typeof) {
    if (typeof.length() > 1) {
      return new int[] {ALOAD, ARETURN};
    }
    char type = typeof.charAt(0);
    switch (type) {
      case 'I':
      case 'Z':
        return new int[] {ILOAD, IRETURN};
      case 'J':
        return new int[] {LLOAD, LRETURN};
      case 'D':
        return new int[] {DLOAD, DRETURN};
      case 'F':
        return new int[] {FLOAD, FRETURN};
      default:
        return new int[] {ALOAD, ARETURN};
    }
  }

  /**
   * 获取类型描述 通过 classname 转换 type descriptor
   *
   * @param classname classname
   * @return type descriptor
   */
  public static String getTypeDescriptor(String classname) {
    return "L" + RegexpUtil.replace(classname, "\\.", "/") + ";";
  }

  public static String getTypeDescriptor(Class<?> clas) {
    return getTypeDescriptor(clas.getName());
  }

  /**
   * 获取泛型签名
   *
   * @param clazz class
   * @param genericTypes 泛型
   * @return 签名
   */
  public static String getSignature(Class<?> clazz, Class<?>[] genericTypes) {
    if (List.class.isAssignableFrom(clazz) && genericTypes.length != 0) {
      return "L"
          + RegexpUtil.replace(clazz.getName(), "\\.", "/")
          + "<L"
          + RegexpUtil.replace(genericTypes[0].getName(), "\\.", "/")
          + ";>;";
    }
    return null;
  }

  /**
   * 比较参数类型是否一致
   *
   * @param types asm的类型({@link Type})
   * @param clazzes java 类型({@link Class})
   * @return boolean
   */
  private static boolean sameType(Type[] types, Class<?>[] clazzes) {
    // 个数不同
    if (types.length != clazzes.length) {
      return false;
    }

    for (int i = 0; i < types.length; i++) {
      if (!Type.getType(clazzes[i]).equals(types[i])) {
        return false;
      }
    }
    return true;
  }

  private static final ConcurrentMap<Method, String[]> METHOD_PARAM_NAME_CACHE =
      new ConcurrentHashMap<>();

  private static final Lock METHOD_PARAM_NAME_LOCK = new ReentrantLock();

  /**
   * 获取方法的参数名
   *
   * @param m 方法
   * @return paramNames
   */
  public static String[] getMethodParamNames(final Method m) {
    if (METHOD_PARAM_NAME_CACHE.containsKey(m)) {
      return METHOD_PARAM_NAME_CACHE.get(m);
    }
    METHOD_PARAM_NAME_LOCK.lock();
    try {
      if (METHOD_PARAM_NAME_CACHE.containsKey(m)) {
        return METHOD_PARAM_NAME_CACHE.get(m);
      }
      final String[] paramNames = new String[m.getParameterTypes().length];
      if (paramNames.length > 0) {
        final Class<?> clazz = m.getDeclaringClass();
        ClassReader cr =
            new ClassReader(
                Objects.requireNonNull(
                    clazz.getResourceAsStream(clazz.getSimpleName() + ".class")));
        cr.accept(
            new ClassVisitor(Opcodes.V1_8) {
              @Override
              public MethodVisitor visitMethod(
                  final int access,
                  final String name,
                  final String desc,
                  final String signature,
                  final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                if (!name.equals(m.getName())
                    || !sameType(args, m.getParameterTypes())) { // 方法名相同并且参数个数相同
                  return null;
                }
                return new MethodVisitor(Opcodes.V1_8) {
                  @Override
                  public void visitLocalVariable(
                      String name,
                      String desc,
                      String signature,
                      Label start,
                      Label end,
                      int index) {
                    int i = index - 1;
                    // 如果是静态方法，则第一就是参数
                    // 如果不是静态方法，则第一个是"this"，然后才是方法的参数
                    if (Modifier.isStatic(m.getModifiers())) {
                      i = index;
                    }
                    if (i >= 0 && i < paramNames.length) {
                      paramNames[i] = name;
                    }
                  }

                  @Override
                  public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                    return null;
                  }

                  @Override
                  public AnnotationVisitor visitAnnotationDefault() {
                    return null;
                  }

                  @Override
                  public void visitAttribute(Attribute arg0) {}

                  @Override
                  public void visitCode() {}

                  @Override
                  public void visitEnd() {}

                  @Override
                  public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {}

                  @Override
                  public void visitMethodInsn(int i, String s, String s2, String s3, boolean is) {}

                  @Override
                  public void visitFrame(
                      int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {}

                  @Override
                  public void visitIincInsn(int arg0, int arg1) {}

                  @Override
                  public void visitInsn(int arg0) {}

                  @Override
                  public void visitIntInsn(int arg0, int arg1) {}

                  @Override
                  public void visitJumpInsn(int arg0, Label arg1) {}

                  @Override
                  public void visitLabel(Label arg0) {}

                  @Override
                  public void visitLdcInsn(Object arg0) {}

                  @Override
                  public void visitLineNumber(int arg0, Label arg1) {}

                  @Override
                  public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {}

                  @Override
                  public void visitMaxs(int arg0, int arg1) {}

                  @Override
                  public void visitMultiANewArrayInsn(String arg0, int arg1) {}

                  @Override
                  public AnnotationVisitor visitParameterAnnotation(
                      int arg0, String arg1, boolean arg2) {
                    return null;
                  }

                  @Override
                  public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label[] arg3) {}

                  @Override
                  public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {}

                  @Override
                  public void visitTypeInsn(int arg0, String arg1) {}

                  @Override
                  public void visitVarInsn(int arg0, int arg1) {}
                };
              }

              @Override
              public void visit(
                  int arg0, int arg1, String arg2, String arg3, String arg4, String[] arg5) {}

              @Override
              public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                return null;
              }

              @Override
              public void visitAttribute(Attribute arg0) {}

              @Override
              public void visitEnd() {}

              @Override
              public FieldVisitor visitField(
                  int arg0, String arg1, String arg2, String arg3, Object arg4) {
                return null;
              }

              @Override
              public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {}

              @Override
              public void visitOuterClass(String arg0, String arg1, String arg2) {}

              @Override
              public void visitSource(String arg0, String arg1) {}
            },
            0);
      }
      METHOD_PARAM_NAME_CACHE.put(m, paramNames);
    } catch (IOException e) {
      log.error(e.getMessage(), e);
      throw new AsmException(e);
    } finally {
      METHOD_PARAM_NAME_LOCK.unlock();
    }
    return METHOD_PARAM_NAME_CACHE.get(m);
  }
}
