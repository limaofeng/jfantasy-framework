package org.jfantasy.framework.spring.mvc.http;

import lombok.Builder;
import lombok.Getter;

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
