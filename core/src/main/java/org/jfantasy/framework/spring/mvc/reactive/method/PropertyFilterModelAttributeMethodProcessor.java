package org.jfantasy.framework.spring.mvc.reactive.method;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.*;
import org.jfantasy.framework.dao.MatchType;
import org.jfantasy.framework.dao.jpa.PropertyFilter;
import org.jfantasy.framework.dao.jpa.PropertyPredicate;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.servlet.HandlerMapping;

public class PropertyFilterModelAttributeMethodProcessor extends MethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return "filter".equals(parameter.getParameterName())
        && PropertyFilter.class.isAssignableFrom(parameter.getParameterType());
  }

  private static boolean isPropertyFilterParameter(Type type) {
    if (type instanceof ParameterizedType) {
      Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
      return actualTypes.length == 1 && actualTypes[0] == PropertyPredicate.class;
    }
    return false;
  }

  @Override
  protected String getParameterName(MethodParameter parameter) {
    return parameter.getParameterName();
  }

  @Override
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
    //    Class<?> parameterType = parameter.getParameterType();
    return PropertyFilter.newFilter();
    //    return BeanUtils.instantiateClass(parameter.getParameterType());
  }

  @Override
  protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
    Map<String, String> variables =
        (Map<String, String>)
            request.getAttribute(
                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
    return (variables != null) ? variables : Collections.<String, String>emptyMap();
  }

  protected final Map<String, String> getUriQueryVariables(NativeWebRequest request) {
    parseQuery(((ServletWebRequest) request).getRequest().getQueryString());
    return new HashMap<>();
  }

  public static Map<String, String[]> parseQuery(String query) {
    Map<String, String[]> params = new LinkedHashMap<>();
    if (StringUtil.isBlank(query)) {
      return params;
    }
    for (String pair : query.split("[;&]")) {
      String[] vs = pair.split("=");
      String key = vs[0];
      String val = vs.length == 1 ? "" : vs[1];
      if (StringUtil.isNotBlank(val)) {
        try {
          val = URLDecoder.decode(val, "utf-8");
        } catch (UnsupportedEncodingException e) {
          throw new IgnoreException(e.getMessage(), e);
        }
      }
      if (!params.containsKey(key)) {
        params.put(key, new String[] {val});
      } else {
        params.put(key, ObjectUtil.join(params.get(key), val));
      }
    }
    return params;
  }

  @Override
  protected void bindRequestParameters(
      WebDataBinder binder, ServerHttpRequest request, MethodParameter parameter) {
    PropertyFilter target = (PropertyFilter) binder.getTarget();
    for (Map.Entry<String, List<String>> param : request.getQueryParams().entrySet()) {
      String paramName = param.getKey();
      List<String> values = param.getValue();
      MatchType matchType = Objects.requireNonNullElse(MatchType.get(paramName), MatchType.EQ);

      if (MatchType.isMultipleValues(matchType)) {
        matchType.build(target, paramName, values.toArray(new String[0]));
      } else {
        matchType.build(target, paramName, values.get(0));
      }
    }
  }

  //  @Override
  //  protected ServletRequest prepareServletRequest(Object target, ServerHttpRequest request,
  // MethodParameter parameter) {
  ////    MockHttpServletRequest mockRequest = withMockRequest(request);
  ////
  ////    for (Map.Entry<String, String> entry : getUriTemplateVariables(request).entrySet()) {
  ////      String parameterName = entry.getKey();
  ////      String value = entry.getValue();
  ////      if (isPropertyFilterModelAttribute(parameterName)) {
  ////        mockRequest.setParameter(parameterName, value);
  ////      }
  ////    }
  ////    for (Map.Entry<String, String[]> entry : nativeRequest.getParameterMap().entrySet()) {
  ////      String parameterName = entry.getKey();
  ////      String[] value = entry.getValue();
  ////      if (isPropertyFilterModelAttribute(parameterName)) {
  ////        mockRequest.setParameter(parameterName, value);
  ////      }
  ////    }
  ////    for (Map.Entry<String, String> entry : getUriQueryVariables(request).entrySet()) {
  ////      String parameterName = entry.getKey();
  ////      String value = entry.getValue();
  ////      if (isPropertyFilterModelAttribute(parameterName)) {
  ////        mockRequest.setParameter(parameterName, value);
  ////      }
  ////    }
  ////    return mockRequest;
  //  }

  private boolean isPropertyFilterModelAttribute(String parameterName) {
    return MatchType.is(parameterName);
  }
}
