package org.jfantasy.framework.util.asm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.FantasyClassLoader;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.PathUtil;
import org.jfantasy.framework.util.common.StreamUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.common.file.FileUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class AsmUtil implements Opcodes {

    private AsmUtil() {
    }

    private static final Log LOG = LogFactory.getLog(AsmUtil.class);

    /**
     * 创建一个java动态bean
     *
     * @param className  新生产className
     * @param properties bean 属性
     * @return 新生成的 class
     */
    public static Class makeClass(String className, Property... properties) {
        return makeClass(className, Object.class.getName(), new Class[0], properties, new MethodInfo[0]);
    }

    public static Class makeClass(String className, Class[] interfaces, Property... properties) {
        return makeClass(className, Object.class.getName(), interfaces, properties, new MethodInfo[0]);
    }

    public static Class makeClass(String className, String superClassName, Property... properties) {
        return makeClass(className, superClassName, new Class[0], properties, new MethodInfo[0]);
    }

    public static Class makeClass(String className, String superClassName, Class[] interfaces, Property... properties) {
        return makeClass(className, superClassName, interfaces, properties, new MethodInfo[0]);
    }

    public static Class makeInterface(String className, AnnotationDescriptor descriptor) {
        return makeInterface(className, new AnnotationDescriptor[]{descriptor}, new Class[0]);
    }

    public static Class makeInterface(String className, AnnotationDescriptor descriptor, Class interfacecls) {
        return makeInterface(className, new AnnotationDescriptor[]{descriptor}, interfacecls);
    }

    public static Class makeInterface(String className, AnnotationDescriptor[] annotDescs, Class... interfaces) {
        ClassWriter cw = new ClassWriter(F_FULL);

        String newClassInternalName = className.replace('.', '/');

        String[] iters = new String[interfaces.length];
        for (int i = 0, len = interfaces.length; i < len; i++) {
            Class inter = interfaces[i];
            iters[i] = inter.getName().replace('.', '/');
        }
        cw.visit(V1_6, ACC_PUBLIC + ACC_ABSTRACT + ACC_INTERFACE, newClassInternalName, null, "java/lang/Object", iters);

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

    public static Class makeClass(String className, String superClassName, Class[] interfaces, Property[] properties, MethodInfo[] methodInfos) {
        ClassWriter cw = new ClassWriter(F_FULL);

        String newClassInternalName = className.replace('.', '/');
        String superClassInternalName = Type.getInternalName(ClassUtil.forName(superClassName));

        String[] iters = new String[interfaces.length];
        for (int i = 0, len = interfaces.length; i < len; i++) {
            Class inter = interfaces[i];
            iters[i] = inter.getName().replace('.', '/');
        }

        AsmContext.getContext().set("className", className);
        AsmContext.getContext().set("superClassName", superClassName);

        Label l0 = new Label();
        Label l1 = new Label();

        /**
         * 注：第一个参数为版本号
         */
        cw.visit(V1_6, ACC_PUBLIC, newClassInternalName, null, superClassInternalName, iters);

        cw.visitSource(RegexpUtil.parseGroup(className, "\\.([A-Za-z0-9_$]+)$", 1) + ".java", null);

        // 构造方法
        MethodVisitor mv = cw.visitMethod(ACC_PUBLIC, "<init>", Type.getMethodDescriptor(Type.getReturnType("V"), new Type[0]), null, null);
        mv.visitLabel(l0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, superClassInternalName, "<init>", Type.getMethodDescriptor(Type.getReturnType("V"), new Type[0]));
        mv.visitInsn(RETURN);
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", getTypeDescriptor(className), null, l0, l1, 0);
        mv.visitMaxs(1, 1);
        mv.visitEnd();

        for (Property property : properties) {
            makeProperty(cw, property);
        }

        for (MethodInfo methodInfo : methodInfos) {
            makeMethod(cw, methodInfo.getMethodName(), methodInfo.getMethodDescriptor(), methodInfo.getSignature(), methodInfo.getMethodCreator());
        }

        // toString方法
        mv = cw.visitMethod(ACC_PUBLIC, "toString", Type.getMethodDescriptor(Type.getType(String.class), new Type[0]), null, null);
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

    protected static Class loadClass(String className, byte[] bytes) {
        try {
            FileUtil.writeFile(bytes, PathUtil.classes() + "/" + className.replace(".", File.separator) + ".class");
            return FantasyClassLoader.getClassLoader().loadClass(PathUtil.classes(), className);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
            try {
                return FantasyClassLoader.getClassLoader().loadClass(bytes, className);
            } catch (ClassNotFoundException ex) {
                LOG.error(e.getMessage(), ex);
                throw new IgnoreException(e.getMessage(), ex);
            }
        } catch (ClassNotFoundException e) {
            LOG.error(e.getMessage(), e);
            throw new IgnoreException(e.getMessage());
        }
    }

    protected static void makeProperty(ClassWriter classWriter, Property property) {

        String fieldName = property.getName();
        String descriptor = Type.getDescriptor(property.getType());
        String signature = getSignature(property.getType(), property.getGenericTypes());

        // 属性
        FieldVisitor fieldVisitor = classWriter.visitField(ACC_PRIVATE, fieldName, descriptor, signature, null);

        // 属性注解
        /*
        AnnotationVisitor annotationVisitor = fieldVisitor.visitAnnotation("Ljavax/persistence/Column;", true);
        annotationVisitor.visit("name","AVATAR");
        annotationVisitor.visit("length",500);
        annotationVisitor.visitEnd();
        */

        fieldVisitor.visitEnd();

        AsmContext.getContext().set("property", property);

        // set方法
        if (property.isWrite()) {
            makeMethod(classWriter, "set" + StringUtil.upperCaseFirst(fieldName), Type.getMethodDescriptor(Type.getReturnType("V"), new Type[]{Type.getType(property.getType())}), property.getGenericTypes().length != 0 ? "(" + getSignature(property.getType(), property.getGenericTypes()) + ")V" : null, property.getSetMethodCreator());
        }

        // get方法
        if (property.isRead()) {
            makeMethod(classWriter, (boolean.class == property.getType() ? "is" : "get") + StringUtil.upperCaseFirst(fieldName), Type.getMethodDescriptor(Type.getType(property.getType()), new Type[0]), property.getGenericTypes().length != 0 ? "()" + getSignature(property.getType(), property.getGenericTypes()) : null, property.getGetMethodCreator());
        }
    }

    protected static void makeMethod(ClassWriter classWriter, String methodName, String methodDescriptor, String signature, MethodCreator methodCreator) {
        MethodVisitor mv = classWriter.visitMethod(ACC_PUBLIC, methodName, methodDescriptor, signature, new String[]{});

        mv.visitCode();

        methodCreator.execute(mv);

        mv.visitEnd();
    }

    public static int[] loadAndReturnOf(String typeof) {
        if (typeof.length() > 1) {
            return new int[]{ALOAD, ARETURN};
        }
        char type = typeof.charAt(0);
        switch (type) {
            case 'I':
            case 'Z':
                return new int[]{ILOAD, IRETURN};
            case 'J':
                return new int[]{LLOAD, LRETURN};
            case 'D':
                return new int[]{DLOAD, DRETURN};
            case 'F':
                return new int[]{FLOAD, FRETURN};
            default:
                return new int[]{ALOAD, ARETURN};
        }
    }

    /**
     * 获取类型描述
     * 通过 classname 转换 type descriptor
     *
     * @param classname classname
     * @return type descriptor
     */
    public static String getTypeDescriptor(String classname) {
        return "L" + RegexpUtil.replace(classname, "\\.", "/") + ";";
    }

    public static String getTypeDescriptor(Class clas) {
        return getTypeDescriptor(clas.getName());
    }

    /**
     * 获取泛型签名
     *
     * @param clazz        class
     * @param genericTypes 泛型
     * @return 签名
     */
    public static String getSignature(Class<?> clazz, Class<?>[] genericTypes) {
        if (List.class.isAssignableFrom(clazz) && genericTypes.length != 0) {
            return "L" + RegexpUtil.replace(clazz.getName(), "\\.", "/") + "<L" + RegexpUtil.replace(genericTypes[0].getName(), "\\.", "/") + ";>;";
        }
        return null;
    }

    /**
     * <p>
     * 比较参数类型是否一致
     * </p>
     *
     * @param types   asm的类型({@link Type})
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

    private static final ConcurrentMap<Method, String[]> methodParamNameCache = new ConcurrentHashMap<Method, String[]>();

    private static final Lock methodParamNameLock = new ReentrantLock();

    /**
     * <p>
     * 获取方法的参数名
     * </p>
     *
     * @param m 方法
     * @return paramNames
     */
    public static String[] getMethodParamNames(final Method m) {
        if (!methodParamNameCache.containsKey(m)) {
            try {
                methodParamNameLock.lock();
                if (methodParamNameCache.containsKey(m)) {
                    return methodParamNameCache.get(m);
                }
                final String[] paramNames = new String[m.getParameterTypes().length];
                if (paramNames.length > 0) {
                    final Class<?> clazz = m.getDeclaringClass();
                    ClassReader cr = new ClassReader(clazz.getResourceAsStream(clazz.getSimpleName() + ".class"));
                    cr.accept(new ClassVisitor() {
                        @Override
                        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                            final Type[] args = Type.getArgumentTypes(desc);
                            if (!name.equals(m.getName()) || !sameType(args, m.getParameterTypes())) {// 方法名相同并且参数个数相同
                                return null;
                            }
                            return new MethodVisitor() {
                                @Override
                                public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
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
                                public void visitAttribute(Attribute arg0) {
                                }

                                @Override
                                public void visitCode() {
                                }

                                @Override
                                public void visitEnd() {
                                }

                                @Override
                                public void visitFieldInsn(int arg0, String arg1, String arg2, String arg3) {
                                }

                                @Override
                                public void visitMethodInsn(int i, String s, String s2, String s3) {

                                }

                                @Override
                                public void visitFrame(int arg0, int arg1, Object[] arg2, int arg3, Object[] arg4) {
                                }

                                @Override
                                public void visitIincInsn(int arg0, int arg1) {
                                }

                                @Override
                                public void visitInsn(int arg0) {
                                }

                                @Override
                                public void visitIntInsn(int arg0, int arg1) {
                                }

                                @Override
                                public void visitJumpInsn(int arg0, Label arg1) {
                                }

                                @Override
                                public void visitLabel(Label arg0) {
                                }

                                @Override
                                public void visitLdcInsn(Object arg0) {
                                }

                                @Override
                                public void visitLineNumber(int arg0, Label arg1) {
                                }

                                @Override
                                public void visitLookupSwitchInsn(Label arg0, int[] arg1, Label[] arg2) {
                                }

                                @Override
                                public void visitMaxs(int arg0, int arg1) {
                                }

                                @Override
                                public void visitMultiANewArrayInsn(String arg0, int arg1) {
                                }

                                @Override
                                public AnnotationVisitor visitParameterAnnotation(int arg0, String arg1, boolean arg2) {
                                    return null;
                                }

                                @Override
                                public void visitTableSwitchInsn(int arg0, int arg1, Label arg2, Label[] arg3) {
                                }

                                @Override
                                public void visitTryCatchBlock(Label arg0, Label arg1, Label arg2, String arg3) {
                                }

                                @Override
                                public void visitTypeInsn(int arg0, String arg1) {
                                }

                                @Override
                                public void visitVarInsn(int arg0, int arg1) {
                                }
                            };
                        }

                        @Override
                        public void visit(int arg0, int arg1, String arg2, String arg3, String arg4, String[] arg5) {
                        }

                        @Override
                        public AnnotationVisitor visitAnnotation(String arg0, boolean arg1) {
                            return null;
                        }

                        @Override
                        public void visitAttribute(Attribute arg0) {
                        }

                        @Override
                        public void visitEnd() {
                        }

                        @Override
                        public FieldVisitor visitField(int arg0, String arg1, String arg2, String arg3, Object arg4) {
                            return null;
                        }

                        @Override
                        public void visitInnerClass(String arg0, String arg1, String arg2, int arg3) {
                        }

                        @Override
                        public void visitOuterClass(String arg0, String arg1, String arg2) {
                        }

                        @Override
                        public void visitSource(String arg0, String arg1) {
                        }
                    }, 0);
                }
                methodParamNameCache.put(m, paramNames);
            } catch (IOException e) {
                LOG.error(e.getMessage(),e);
                throw new AsmException(e);
            } finally {
                methodParamNameLock.unlock();
            }
        }
        return methodParamNameCache.get(m);

    }

    public static String trace(Class<?> clazz) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        try {
            ClassReader reader = new ClassReader(clazz.getName());
            reader.accept(new TraceClassVisitor(writer), 0);
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        } finally {
            StreamUtil.closeQuietly(writer);
        }
        return stringWriter.toString();
    }

}