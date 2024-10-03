/*
 * Copyright (c) 2024 Asany
 *
 * Licensed under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.asany.net/licenses/MIT
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.asany.jfantasy.framework.spring.mvc.reactive.method;

import java.lang.annotation.Annotation;
import java.util.*;
import net.asany.jfantasy.framework.spring.mvc.bind.annotation.FormModel;
import net.asany.jfantasy.framework.spring.mvc.util.MapWapper;
import net.asany.jfantasy.framework.util.common.ClassUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.HandlerMapping;
import reactor.core.publisher.Mono;

public abstract class MethodArgumentResolver implements HandlerMethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(FormModel.class);
  }

  @Override
  public @NotNull Mono<Object> resolveArgument(
      @NotNull MethodParameter parameter,
      BindingContext bindingContext,
      @NotNull ServerWebExchange exchange) {
    String name = getParameterName(parameter);

    Model model = bindingContext.getModel();

    Object target =
        model.containsAttribute(name)
            ? model.getAttribute(name)
            : createAttribute(name, parameter, bindingContext, exchange);

    WebDataBinder binder = bindingContext.createDataBinder(exchange, target, name);
    target = binder.getTarget();
    if (target != null) {
      bindRequestParameters(binder, exchange.getRequest(), parameter);
      validateIfApplicable(binder, parameter);
      if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
        return Mono.error(new BindException(binder.getBindingResult()));
      }
    }

    target = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType());
    if (StringUtil.isNotBlank(name)) {
      model.addAttribute(name, target);
    }
    return target == null ? Mono.empty() : Mono.just(target);
  }

  protected String getParameterName(MethodParameter parameter) {
    return Objects.requireNonNull(parameter.getParameterAnnotation(FormModel.class)).value();
  }

  protected Object createAttribute(
      String attributeName,
      MethodParameter parameter,
      BindingContext bindingContext,
      ServerWebExchange exchange) {

    String value = getRequestValueForAttribute(attributeName, exchange);

    if (value != null) {
      Object attribute =
          createAttributeFromRequestValue(
              value, attributeName, parameter, bindingContext, exchange);
      if (attribute != null) {
        return attribute;
      }
    }
    Class<?> parameterType = parameter.getParameterType();
    if (parameterType.isArray() || List.class.isAssignableFrom(parameterType)) {
      return ClassUtil.getBeanInfo(ArrayList.class);
    }
    if (Set.class.isAssignableFrom(parameterType)) {
      return ClassUtil.getBeanInfo(HashSet.class);
    }

    if (MapWapper.class.isAssignableFrom(parameterType)) {
      return ClassUtil.getBeanInfo(MapWapper.class);
    }

    return BeanUtils.instantiateClass(parameter.getParameterType());
  }

  String getRequestValueForAttribute(String attributeName, ServerWebExchange exchange) {
    MultiValueMap<String, String> variables = exchange.getRequest().getQueryParams();
    if (StringUtils.hasText(variables.getFirst(attributeName))) {
      return variables.getFirst(attributeName);
    }
    return null;
  }

  protected Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
    Map<String, String> variables =
        (Map<String, String>)
            request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    return (variables != null) ? variables : Collections.<String, String>emptyMap();
  }

  Object createAttributeFromRequestValue(
      String sourceValue,
      String attributeName,
      MethodParameter parameter,
      BindingContext bindingContext,
      ServerWebExchange exchange) {
    DataBinder binder = bindingContext.createDataBinder(exchange, attributeName);
    ConversionService conversionService = binder.getConversionService();
    if (conversionService != null) {
      TypeDescriptor source = TypeDescriptor.valueOf(String.class);
      TypeDescriptor target = new TypeDescriptor(parameter);
      if (conversionService.canConvert(source, target)) {
        return binder.convertIfNecessary(sourceValue, parameter.getParameterType(), parameter);
      }
    }
    return null;
  }

  protected void bindRequestParameters(
      WebDataBinder binder, ServerHttpRequest request, MethodParameter parameter) {
    //    Class<?> targetType = binder.getTarget().getClass();
    //    ServletRequest servletRequest = prepareServletRequest(binder.getTarget(), request,
    // parameter);
    //    WebDataBinder simpleBinder = binderFactory.createBinder(request, null, null);
    //
    //    if (Collection.class.isAssignableFrom(targetType)) { // bind collection
    //
    //      Type type = parameter.getGenericParameterType();
    //      Class<?> componentType = Object.class;
    //
    //      Collection target = (Collection) binder.getTarget();
    //
    //      if (type instanceof ParameterizedType) {
    //        componentType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    //      }
    //
    //      if (parameter.getParameterType().isArray()) {
    //        componentType = parameter.getParameterType().getComponentType();
    //      }
    //
    //      for (Object key : servletRequest.getParameterMap().keySet()) {
    //        String prefixName = getPrefixName((String) key);
    //        if (isSimpleComponent(prefixName)) { // bind simple type
    //          Map<String, Object> paramValues = WebUtils.getParametersStartingWith(servletRequest,
    // prefixName);
    //          for (Object value : paramValues.values()) {
    //            target.add(simpleBinder.convertIfNecessary(value, componentType));
    //          }
    //        } else {
    //
    //          Object component = BeanUtils.instantiate(componentType);
    //          WebDataBinder componentBinder = binderFactory.createBinder(request, component,
    // null);
    //          component = componentBinder.getTarget();
    //
    //          if (component != null) {
    //            ServletRequestParameterPropertyValues pvs = new
    // ServletRequestParameterPropertyValues(servletRequest, prefixName, "");
    //            componentBinder.bind(pvs);
    //            validateIfApplicable(componentBinder, parameter);
    //            if (componentBinder.getBindingResult().hasErrors() &&
    // isBindExceptionRequired(componentBinder, parameter)) {
    //              throw new BindException(componentBinder.getBindingResult());
    //            }
    //            target.add(component);
    //          }
    //        }
    //      }
    //    } else if (MapWapper.class.isAssignableFrom(targetType)) {
    //
    //      Type type = parameter.getGenericParameterType();
    //      Class<?> keyType = Object.class;
    //      Class<?> valueType = Object.class;
    //
    //      if (type instanceof ParameterizedType) {
    //        keyType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[0];
    //        valueType = (Class<?>) ((ParameterizedType) type).getActualTypeArguments()[1];
    //      }
    //
    //      Map target = new HashMap();
    //      ((MapWapper) binder.getTarget()).setInnerMap(target);
    //
    //      for (Object key : servletRequest.getParameterMap().keySet()) {
    //        String prefixName = getPrefixName((String) key);
    //
    //        Object keyValue = simpleBinder.convertIfNecessary(getMapKey(prefixName), keyType);
    //
    //        if (isSimpleComponent(prefixName)) { // bind simple type
    //          Map<String, Object> paramValues = WebUtils.getParametersStartingWith(servletRequest,
    // prefixName);
    //
    //          for (Object value : paramValues.values()) {
    //            target.put(keyValue, simpleBinder.convertIfNecessary(value, valueType));
    //          }
    //        } else {
    //          Object component = BeanUtils.instantiate(valueType);
    //          WebDataBinder componentBinder = binderFactory.createBinder(request, component,
    // null);
    //          component = componentBinder.getTarget();
    //
    //          if (component != null) {
    //            ServletRequestParameterPropertyValues pvs = new
    // ServletRequestParameterPropertyValues(servletRequest, prefixName, "");
    //            componentBinder.bind(pvs);
    //
    //            validateComponent(componentBinder, parameter);
    //
    //            target.put(keyValue, component);
    //          }
    //        }
    //      }
    //    } else { // bind model
    //      ServletRequestDataBinder servletBinder = (ServletRequestDataBinder) binder;
    //      servletBinder.bind(servletRequest);
    //    }
  }

  private Object getMapKey(String prefixName) {
    String key = prefixName;
    if (key.startsWith("['")) {
      key = key.replaceAll("\\[\'", "").replaceAll("\'\\]", "");
    }
    if (key.startsWith("[\"")) {
      key = key.replaceAll("\\[\"", "").replaceAll("\"\\]", "");
    }
    if (key.endsWith(".")) {
      key = key.substring(0, key.length() - 1);
    }
    return key;
  }

  private boolean isSimpleComponent(String prefixName) {
    return !prefixName.endsWith(".");
  }

  private String getPrefixName(String name) {

    int begin = 0;

    int end = name.indexOf("]") + 1;

    if (name.contains("].")) {
      end = end + 1;
    }

    return name.substring(begin, end);
  }

  protected Map<String, String[]> prepareServletRequest(
      Object target, ServerHttpRequest request, MethodParameter parameter) {

    //
    //    String modelPrefixName =
    //        Objects.requireNonNull(parameter.getParameterAnnotation(FormModel.class)).value();

    //    ServerHttpRequest nativeRequest = null; // (HttpServletRequest)
    // request.getNativeRequest();

    //    MockHttpServletRequest mockRequest = withMockRequest(nativeRequest);
    //
    //    for (Entry<String, String> entry : getUriTemplateVariables(request).entrySet()) {
    //      String parameterName = entry.getKey();
    //      String value = entry.getValue();
    //      if (isFormModelAttribute(parameterName, modelPrefixName)) {
    //        mockRequest.setParameter(getNewParameterName(parameterName, modelPrefixName), value);
    //      }
    //    }
    //
    //    for (Entry<String, String[]> parameterEntry : nativeRequest.getParameterMap().entrySet())
    // {
    //      String parameterName = parameterEntry.getKey();
    //      String[] value = parameterEntry.getValue();
    //      if (isFormModelAttribute(parameterName, modelPrefixName)) {
    //        mockRequest.setParameter(getNewParameterName(parameterName, modelPrefixName), value);
    //      }
    //        }

    return new HashMap<>();
  }

  private String getNewParameterName(String parameterName, String modelPrefixName) {
    int modelPrefixNameLength = modelPrefixName.length();

    if (parameterName.charAt(modelPrefixNameLength) == '.') {
      return parameterName.substring(modelPrefixNameLength + 1);
    }

    if (parameterName.charAt(modelPrefixNameLength) == '[') {
      return parameterName.substring(modelPrefixNameLength);
    }
    throw new IllegalArgumentException(
        "illegal request parameter, can not binding to @FormBean(" + modelPrefixName + ")");
  }

  private boolean isFormModelAttribute(String parameterName, String modelPrefixName) {
    int modelPrefixNameLength = modelPrefixName.length();

    if (parameterName.length() == modelPrefixNameLength) {
      return false;
    }

    if (!parameterName.startsWith(modelPrefixName)) {
      return false;
    }

    char ch = parameterName.charAt(modelPrefixNameLength);

    return ch == '.' || ch == '[';
  }

  protected void validateComponent(WebDataBinder binder, MethodParameter parameter)
      throws BindException {

    boolean validateParameter = validateParameter(parameter);
    Annotation[] annotations =
        Objects.requireNonNull(binder.getTarget()).getClass().getAnnotations();
    for (Annotation annot : annotations) {
      if (annot.annotationType().getSimpleName().startsWith("Valid") && validateParameter) {
        Object hints = AnnotationUtils.getValue(annot);
        binder.validate(hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
      }
    }

    if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
      throw new BindException(binder.getBindingResult());
    }
  }

  private boolean validateParameter(MethodParameter parameter) {
    Annotation[] annotations = parameter.getParameterAnnotations();
    for (Annotation annot : annotations) {
      if (annot.annotationType().getSimpleName().startsWith("Valid")) {
        return true;
      }
    }

    return false;
  }

  protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
    Annotation[] annotations = parameter.getParameterAnnotations();
    for (Annotation annot : annotations) {
      if (annot.annotationType().getSimpleName().startsWith("Valid")) {
        Object hints = AnnotationUtils.getValue(annot);
        binder.validate(hints instanceof Object[] ? (Object[]) hints : new Object[] {hints});
      }
    }
  }

  protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
    int i = parameter.getParameterIndex();
    Class<?>[] paramTypes = Objects.requireNonNull(parameter.getMethod()).getParameterTypes();
    boolean hasBindingResult =
        paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]);

    return !hasBindingResult;
  }

  //  protected MockHttpServletRequest withMockRequest(HttpServletRequest nativeRequest) {
  //    MultipartRequest multipartRequest =
  //        WebUtils.getNativeRequest(nativeRequest, MultipartRequest.class);
  //    MockHttpServletRequest mockRequest;
  //    if (multipartRequest != null) {
  //      MockMultipartHttpServletRequest mockMultipartRequest = new
  // MockMultipartHttpServletRequest();
  //      mockMultipartRequest.getMultiFileMap().putAll(multipartRequest.getMultiFileMap());
  //      mockRequest = mockMultipartRequest;
  //    } else {
  //      mockRequest = new MockHttpServletRequest();
  //    }
  //    return mockRequest;
  //  }
}
