package net.asany.jfantasy.framework.util.asm;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class MethodInfo {
  private String methodName;
  @Builder.Default private int symbolTable = ACC_PUBLIC;
  private String methodDescriptor;
  private String signature;
  private MethodCreator methodCreator;
}
