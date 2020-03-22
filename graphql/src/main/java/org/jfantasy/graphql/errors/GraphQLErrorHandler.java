package org.jfantasy.graphql.errors;

import graphql.GraphQLError;
import org.jfantasy.framework.error.ErrorUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.lang.reflect.UndeclaredThrowableException;

/**
 * @author limaofeng
 * @version V1.0
 * @Description: TODO
 * @date 2020/3/22 4:15 下午
 */
@Component
public class GraphQLErrorHandler {

    @ExceptionHandler(value = Exception.class)
    public GraphQLError transform(Exception e) {
        if (e instanceof GraphQLError) {
            return (GraphQLError) e;
        }
        DefaultGraphQLError error = new DefaultGraphQLError();
        if (e instanceof UndeclaredThrowableException) {
            e = (Exception) ((UndeclaredThrowableException) e).getUndeclaredThrowable();
        }
        if (e instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException ex = ((MethodArgumentNotValidException) e);
            ErrorUtils.fill(error, ex);
            error.addPath(ex.getParameter().getMethod().getName() + "." + ex.getBindingResult().getObjectName());
        } else {
            ErrorUtils.fill(error, e);
        }
        return error;
    }

}
