package org.jfantasy.framework.error;

import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/22 4:47 下午
 */
public class ErrorUtils {

    public static void fill(ErrorResponse error, ValidationException exception) {
        error.setCode(exception.getCode());
        error.setMessage(exception.getMessage());
        if (exception.getData() != null && !exception.getData().isEmpty()) {
            exception.getData().forEach((k, v) -> error.addData(k, v));
        }
    }

    public static void fill(ErrorResponse error, Exception exception) {
        if (exception instanceof ValidationException) {
            fill(error, (ValidationException) exception);
            return;
        }
        error.setCode("42200");
        error.setMessage(exception.getMessage());
    }

    public static void fill(ErrorResponse error, MethodArgumentNotValidException exception) {
        error.setCode("42200");
        error.setMessage("输入的数据不合法,详情见 fields 字段");
        for (FieldError fieldError : exception.getBindingResult().getFieldErrors()) {
            error.addFieldError(fieldError.getField(), fieldError.getDefaultMessage());
        }
    }

}
