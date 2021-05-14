package org.jfantasy.framework.error;

import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.PropertiesHelper;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * 异常工具类
 *
 * @author limaofeng
 * @version V1.0
 * @date 2020/3/22 4:47 下午
 */
public class ErrorUtils {

    private static final PropertiesHelper HELPER = PropertiesHelper.load("error_codes.properties");

    public static String errorCode(Exception exception) {
        Class<?> errorClass = exception.getClass();
        String errorCode;
        do {
            errorCode = HELPER.getProperty(errorClass.getName());
            errorClass = errorClass.getSuperclass();
            if (errorClass == Exception.class) {
                break;
            }
        } while (errorCode == null);

        if (errorCode != null) {
            return errorCode;
        }

        if (exception.getCause() instanceof Exception && exception.getCause() != exception) {
            return errorCode((Exception) exception.getCause());
        }

        return HELPER.getProperty(Exception.class.getName());
    }

    public static void fill(ErrorResponse error, ValidationException exception) {
        error.setCode(ObjectUtil.defaultValue(exception.getCode(), errorCode(exception)));
        error.setMessage(exception.getMessage());
        if (exception.getData() != null && !exception.getData().isEmpty()) {
            exception.getData().forEach(error::addData);
        }
    }

    public static void fill(ErrorResponse error, Exception exception) {
        if (exception instanceof ValidationException) {
            fill(error, (ValidationException) exception);
            return;
        }
        error.setCode(ErrorUtils.errorCode(exception));
        error.setMessage(exception.getMessage());
    }

    public static void fill(ErrorResponse error, MethodArgumentNotValidException exception) {
        error.setCode(ErrorUtils.errorCode(exception));
        error.setMessage("输入的数据不合法,详情见 fields 字段");
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            error.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

}
