package net.asany.jfantasy.framework.error;

import java.util.*;
import net.asany.jfantasy.framework.util.common.PropertiesHelper;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.support.WebExchangeBindException;

/**
 * 异常工具类
 *
 * @author limaofeng
 * @version V1.0
 */
public class ErrorUtils {

  private static final PropertiesHelper HELPER = PropertiesHelper.load("error_codes.properties");
  private static final String ERROR_PROPERTY_CODE = "code";
  private static final String ERROR_PROPERTY_MESSAGE = "message";
  private static final String ERROR_PROPERTY_EXTRA_DATA = "data";
  private static final String ERROR_PROPERTY_FIELD_ERRORS = "errors";
  private static final String ERROR_PROPERTY_ERROR = "error";
  private static final String ERROR_PROPERTY_STATUS = "status";

  private static final boolean WEB_CLASS_AVAILABLE =
      isClassAvailable("org.springframework.web.servlet.config.annotation.EnableWebMvc");
  private static final boolean WEB_FLUX_CLASS_AVAILABLE =
      isClassAvailable("org.springframework.web.reactive.config.EnableWebFlux");

  public static String errorCode(Throwable exception) {
    Class<?> errorClass = exception.getClass();
    String errorCode =
        exception instanceof ValidationException
            ? ((ValidationException) exception).getCode()
            : null;
    while (errorCode == null) {
      errorCode = HELPER.getProperty(errorClass.getName());
      errorClass = errorClass.getSuperclass();
      if (errorClass == Exception.class) {
        break;
      }
    }

    if (errorCode != null) {
      return errorCode;
    }

    if (exception.getCause() instanceof Exception && exception.getCause() != exception) {
      return errorCode(exception.getCause());
    }

    return HELPER.getProperty(Exception.class.getName());
  }

  public static void populateErrorAttributesFromException(
      Map<String, Object> errorAttributes, Throwable throwable) {
    String errorCode = errorCode(throwable);

    errorAttributes.put(ERROR_PROPERTY_CODE, errorCode);
    errorAttributes.put(ERROR_PROPERTY_STATUS, 400);
    errorAttributes.put(ERROR_PROPERTY_ERROR, throwable.getClass().getSimpleName());
    errorAttributes.put(ERROR_PROPERTY_MESSAGE, throwable.getMessage());

    if (throwable instanceof ValidationException e) {
      extractValidationExceptionAttributes(errorAttributes, e);
    } else if (WEB_CLASS_AVAILABLE && throwable instanceof MethodArgumentNotValidException e) {
      addBindingResultErrorMessage(errorAttributes, e.getBindingResult());
    } else if (WEB_FLUX_CLASS_AVAILABLE && throwable instanceof WebExchangeBindException e) {
      addBindingResultErrorMessage(errorAttributes, e.getBindingResult());
    }
  }

  private static boolean isClassAvailable(String className) {
    try {
      Class.forName(className);
      return true;
    } catch (ClassNotFoundException e) {
      return false;
    }
  }

  private static void addBindingResultErrorMessage(
      Map<String, Object> errorAttributes, BindingResult result) {
    errorAttributes.put(
        ERROR_PROPERTY_MESSAGE,
        "Validation failed for object='"
            + result.getObjectName()
            + "'. "
            + "Error count: "
            + result.getErrorCount());
    if (result.hasFieldErrors()) {
      List<FieldValidationError> fields = new ArrayList<>();
      for (FieldError fieldError : result.getFieldErrors()) {
        fields.add(
            FieldValidationError.builder()
                .field(fieldError.getField())
                .rejectedValue(fieldError.getRejectedValue())
                .objectName(fieldError.getObjectName())
                .path(
                    Objects.requireNonNull(fieldError.getCodes())[0].substring(
                        Objects.requireNonNull(fieldError.getCode()).length() + 1))
                .code(fieldError.getCode())
                .message(fieldError.getDefaultMessage())
                .build());
      }
      errorAttributes.put(ERROR_PROPERTY_FIELD_ERRORS, fields);
    }
  }

  public static Map<String, Object> getExtractErrorAttributes(Throwable throwable) {
    Map<String, Object> errorAttributes = new HashMap<>();
    populateErrorAttributesFromException(errorAttributes, throwable);
    return errorAttributes;
  }

  public static void populateErrorAttributesFromException(ErrorResponse error, Throwable e) {
    Map<String, Object> errorAttributes = getExtractErrorAttributes(e);

    error.setCode((String) errorAttributes.get(ERROR_PROPERTY_CODE));
    error.setError((String) errorAttributes.get(ERROR_PROPERTY_ERROR));
    error.setStatus((Integer) errorAttributes.get(ERROR_PROPERTY_STATUS));
    error.setMessage((String) errorAttributes.get(ERROR_PROPERTY_MESSAGE));

    if (errorAttributes.containsKey(ERROR_PROPERTY_EXTRA_DATA))
      //noinspection unchecked
      error.addData((Map<String, Object>) errorAttributes.get(ERROR_PROPERTY_EXTRA_DATA));

    if (errorAttributes.containsKey(ERROR_PROPERTY_FIELD_ERRORS)) {
      //noinspection unchecked
      error.setFieldErrors(
          (List<FieldValidationError>) errorAttributes.get(ERROR_PROPERTY_FIELD_ERRORS));
    }
  }

  private static void extractValidationExceptionAttributes(
      Map<String, Object> error, ValidationException exception) {

    if (exception.hasFieldErrors()) {
      addBindingResultErrorMessage(error, exception.getBindingResult());
    }

    if (exception.getData() != null && !exception.getData().isEmpty()) {
      error.put(ERROR_PROPERTY_EXTRA_DATA, exception.getData());
    }
  }
}
