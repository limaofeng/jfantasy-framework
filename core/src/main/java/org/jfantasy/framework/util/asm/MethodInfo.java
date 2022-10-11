package org.jfantasy.framework.util.asm;

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
  MethodCreator methodCreator;

  public MethodInfo(
      String methodName, String methodDescriptor, String signature, MethodCreator methodCreator) {
    this.methodName = methodName;
    this.methodDescriptor = methodDescriptor;
    this.signature = signature;
    this.methodCreator = methodCreator;
  }
}
