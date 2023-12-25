package net.asany.jfantasy.graphql.error;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import org.aspectj.lang.annotation.Aspect;
import org.jetbrains.annotations.NotNull;
import org.springframework.aop.MethodBeforeAdvice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;

/**
 * GraphQLResolver 支持 Validated 注解验证
 *
 * @author limaofeng
 * @version V1.0
 */
@Aspect
@Component
public class GraphQLResolverAdvice implements MethodBeforeAdvice {

  @Autowired private SmartValidator validator;

  public final DataBinder createBinder(Object target, String objectName) {
    DataBinder binder = new DataBinder(target, objectName);
    binder.setValidator(validator);
    return binder;
  }

  protected void validateIfApplicable(DataBinder binder, MethodParameter parameter) {
    Annotation[] annotations = parameter.getParameterAnnotations();
    for (Annotation ann : annotations) {
      Validated validatedAnn = AnnotationUtils.getAnnotation(ann, Validated.class);
      if (validatedAnn != null || ann.annotationType().getSimpleName().startsWith("Valid")) {
        Object hints =
            (validatedAnn != null ? validatedAnn.value() : AnnotationUtils.getValue(ann));
        Object[] validationHints =
            (hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
        binder.validate(validationHints);
        break;
      }
    }
  }

  @Override
  public void before(Method method, Object @NotNull [] args, Object target) throws Throwable {
    Parameter[] parameters = method.getParameters();
    for (int i = 0; i < parameters.length; i++) {
      MethodParameter parameter = MethodParameter.forParameter(parameters[i]);
      DataBinder binder = createBinder(args[i], ClassUtil.getParameterName(parameters[i]));
      validateIfApplicable(binder, parameter);
      if (binder.getBindingResult().hasErrors()) {
        throw new MethodArgumentNotValidException(parameter, binder.getBindingResult());
      }
    }
  }
}
