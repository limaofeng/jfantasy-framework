package net.asany.jfantasy.framework.error;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class FieldValidationError {
  private String path;
  private String code;
  private String objectName;
  private String field;
  private Object rejectedValue;
  private String message;
}
