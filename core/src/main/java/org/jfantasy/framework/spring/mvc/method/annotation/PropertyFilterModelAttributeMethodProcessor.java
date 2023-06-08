package org.jfantasy.framework.spring.mvc.method.annotation;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URLDecoder;
import java.util.*;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.jfantasy.framework.dao.MatchType;
import org.jfantasy.framework.dao.jpa.PropertyPredicate;
import org.jfantasy.framework.error.IgnoreException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.HandlerMapping;

public class PropertyFilterModelAttributeMethodProcessor extends MethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return "filters".equals(parameter.getParameterName())
        && List.class.isAssignableFrom(parameter.getParameterType())
        && isPropertyFilterParameter(parameter.getGenericParameterType());
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
      WebDataBinderFactory binderFactory,
      NativeWebRequest request)
      throws Exception {
    String value = getRequestValueForAttribute(attributeName, request);
    if (value != null) {
      Object attribute =
          createAttributeFromRequestValue(value, attributeName, parameter, binderFactory, request);
      if (attribute != null) {
        return attribute;
      }
    }
    Class<?> parameterType = parameter.getParameterType();
    if (parameterType.isArray() || List.class.isAssignableFrom(parameterType)) {
      return ArrayList.class.newInstance();
    }
    return BeanUtils.instantiateClass(parameter.getParameterType());
  }

  @SuppressWarnings("unchecked")
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
      ModelAndViewContainer mavContainer,
      WebDataBinderFactory binderFactory,
      WebDataBinder binder,
      NativeWebRequest request,
      MethodParameter parameter)
      throws Exception {
    ServletRequest servletRequest = prepareServletRequest(binder.getTarget(), request, parameter);
    @SuppressWarnings("unchecked")
    List<Object> target = (List<Object>) binder.getTarget();
    for (String paramName : servletRequest.getParameterMap().keySet()) {
      String[] values = request.getParameterValues(paramName);
      MatchType matchType = MatchType.get(paramName);
      assert matchType != null;
      // if (matchType.isNone()) {
      // target.add(new PropertyFilter(paramName));
      // } else if (matchType.isMulti()) {
      // List<String> tValues = new ArrayList<>();
      // for (String val : values) {
      // tValues.addAll(Arrays.asList(StringUtil.tokenizeToStringArray(val)));
      // }
      // target.add(new PropertyFilter(paramName, tValues.toArray(new
      // String[tValues.size()])));
      // } else if (values.length != 0 && StringUtil.isNotBlank(values[0])) {
      // target.add(new PropertyFilter(paramName, values[0]));
      // }
    }
  }

  @Override
  protected ServletRequest prepareServletRequest(
      Object target, NativeWebRequest request, MethodParameter parameter) {
    HttpServletRequest nativeRequest = (HttpServletRequest) request.getNativeRequest();
    MockHttpServletRequest mockRequest = withMockRequest(nativeRequest);

    for (Map.Entry<String, String> entry : getUriTemplateVariables(request).entrySet()) {
      String parameterName = entry.getKey();
      String value = entry.getValue();
      if (isPropertyFilterModelAttribute(parameterName)) {
        mockRequest.setParameter(parameterName, value);
      }
    }
    for (Map.Entry<String, String[]> entry : nativeRequest.getParameterMap().entrySet()) {
      String parameterName = entry.getKey();
      String[] value = entry.getValue();
      if (isPropertyFilterModelAttribute(parameterName)) {
        mockRequest.setParameter(parameterName, value);
      }
    }
    for (Map.Entry<String, String> entry : getUriQueryVariables(request).entrySet()) {
      String parameterName = entry.getKey();
      String value = entry.getValue();
      if (isPropertyFilterModelAttribute(parameterName)) {
        mockRequest.setParameter(parameterName, value);
      }
    }
    return mockRequest;
  }

  private boolean isPropertyFilterModelAttribute(String parameterName) {
    return MatchType.is(parameterName);
  }
}
