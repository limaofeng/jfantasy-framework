package org.jfantasy.framework.util.asm;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.objectweb.asm.Label;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

/**
 * 类属性信息 - 用于生成动态类时，属性的描述信息
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-5-31 上午09:56:39
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Property {

  /** 属性名称 */
  private String name;

  /** 属性类型 */
  private Class type;

  /** 类型描述 */
  private String descriptor;

  private String signature;

  /** 泛型 */
  @Builder.Default private Class[] genericTypes = new Class[0];

  /** 是否可以写入(set操作) */
  @Builder.Default private boolean write = true;

  /** 是否可以读取(get操作) */
  @Builder.Default private boolean read = true;

  @Builder.Default
  private MethodCreator getMethodCreator =
      mv -> {
        String className = AsmContext.getContext().get("className", String.class);
        Property property = AsmContext.getContext().get("property", Property.class);

        String newClassInternalName = className.replace('.', '/');

        Label l0 = new Label();
        Label l1 = new Label();

        String fieldName = property.getName();
        String descriptor = property.getDescriptor();
        int[] loadAndReturnOf = AsmUtil.loadAndReturnOf(descriptor);

        mv.visitLabel(l0);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, newClassInternalName, fieldName, descriptor);
        mv.visitInsn(loadAndReturnOf[1]); // ARETURN
        mv.visitLabel(l1);
        mv.visitLocalVariable("this", AsmUtil.getTypeDescriptor(className), null, l0, l1, 0);

        mv.visitMaxs(1, 1);
      };

  @Builder.Default
  private MethodCreator setMethodCreator =
      mv -> {
        String className = AsmContext.getContext().get("className", String.class);
        Property property = AsmContext.getContext().get("property", Property.class);

        String newClassInternalName = className.replace('.', '/');

        String fieldName = property.getName();
        String descriptor = property.getDescriptor();
        String signature = property.getSignature();
        int[] loadAndReturnOf = AsmUtil.loadAndReturnOf(descriptor);

        Label l0 = new Label();
        Label l1 = new Label();
        Label l2 = new Label();

        mv.visitLabel(l0);
        mv.visitVarInsn(Opcodes.ALOAD, Opcodes.F_FULL);
        mv.visitVarInsn(loadAndReturnOf[0], Opcodes.F_APPEND);
        mv.visitFieldInsn(Opcodes.PUTFIELD, newClassInternalName, fieldName, descriptor);
        mv.visitLabel(l1);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitLabel(l2);
        mv.visitLocalVariable("this", AsmUtil.getTypeDescriptor(className), null, l0, l2, 0);
        mv.visitLocalVariable(fieldName, descriptor, signature, l0, l2, 1);
        mv.visitMaxs(2, 2);
      };

  public Property(String name, Class type) {
    super();
    this.name = name;
    this.type = type;
  }

  public Property(String name, Class type, boolean read, boolean write) {
    super();
    this.name = name;
    this.type = type;
    this.read = read;
    this.write = write;
  }

  public Property(String name, Class type, Class[] genericTypes) {
    super();
    this.name = name;
    this.type = type;
    this.genericTypes = genericTypes;
  }

  public Property(String name, Class type, Class[] genericTypes, boolean read, boolean write) {
    super();
    this.name = name;
    this.type = type;
    this.genericTypes = genericTypes;
    this.read = read;
    this.write = write;
    this.descriptor = Type.getDescriptor(type);
    this.signature = AsmUtil.getSignature(type, genericTypes);
  }

  public String getName() {
    return name;
  }

  public Class getType() {
    return type;
  }

  public String getSignature() {
    return ObjectUtil.defaultValue(
        signature,
        () -> type != null ? signature = AsmUtil.getSignature(type, genericTypes) : null);
  }

  public String getDescriptor() {
    return ObjectUtil.defaultValue(descriptor, () -> descriptor = Type.getDescriptor(type));
  }

  public boolean isWrite() {
    return write;
  }

  public boolean isRead() {
    return read;
  }

  public Class[] getGenericTypes() {
    return genericTypes;
  }

  public MethodCreator getGetMethodCreator() {
    return getMethodCreator;
  }

  public void setGetMethodCreator(MethodCreator getMethodCreator) {
    this.getMethodCreator = getMethodCreator;
  }

  public MethodCreator getSetMethodCreator() {
    return setMethodCreator;
  }

  public void setSetMethodCreator(MethodCreator setMethodCreator) {
    this.setMethodCreator = setMethodCreator;
  }
}
