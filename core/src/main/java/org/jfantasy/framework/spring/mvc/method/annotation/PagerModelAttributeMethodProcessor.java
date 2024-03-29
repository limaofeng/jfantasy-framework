package org.jfantasy.framework.spring.mvc.method.annotation;

import java.util.List;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import org.jfantasy.framework.dao.OrderBy;
import org.jfantasy.framework.dao.Page;
import org.jfantasy.framework.dao.Pagination;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

public class PagerModelAttributeMethodProcessor extends MethodArgumentResolver {

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return Page.class.isAssignableFrom(parameter.getParameterType());
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
      return new Pagination<>();
    }
    return BeanUtils.instantiateClass(parameter.getParameterType());
  }

  @Override
  protected void bindRequestParameters(
      ModelAndViewContainer mavContainer,
      WebDataBinderFactory binderFactory,
      WebDataBinder binder,
      NativeWebRequest request,
      MethodParameter parameter) {
    ServletRequest servletRequest = prepareServletRequest(binder.getTarget(), request, parameter);
    Page target = (Page) binder.getTarget();
    assert target != null;
    for (String paramName : servletRequest.getParameterMap().keySet()) {
      String value = servletRequest.getParameter(paramName);
      if (StringUtil.isBlank(value)) {
        continue;
      }
      if ("limit".equalsIgnoreCase(paramName)) {
        Integer[] limits = new Integer[0];
        for (String s : StringUtil.tokenizeToStringArray(value)) {
          limits = ObjectUtil.join(limits, Integer.valueOf(s));
        }
        if (limits.length > 2) {
          throw new RestException(" limit 参数格式不正确,格式为: limit=0,5 or limit=5");
        }
        if (limits.length == 1) {
          limits = new Integer[] {0, limits[0]};
        }
        target.setOffset(limits[0]);
        target.setPageSize(limits[1]);
      } else if ("page".equalsIgnoreCase(paramName)) {
        target.setCurrentPage(Integer.parseInt(value));
      } else if ("per_page".equalsIgnoreCase(paramName)) {
        target.setPageSize(Integer.parseInt(value));
      } else if ("order_by".equalsIgnoreCase(paramName)) {
        String[] sort = value.split("_");
        target.setOrderBy(OrderBy.newOrderBy(sort[0], OrderBy.Direction.valueOf(sort[1])));
      }
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
      if (isPagerModelAttribute(parameterName, getModelNames())) {
        mockRequest.setParameter(parameterName, value);
      }
    }
    for (Map.Entry<String, String[]> entry : nativeRequest.getParameterMap().entrySet()) {
      String parameterName = entry.getKey();
      String[] value = entry.getValue();
      if (isPagerModelAttribute(parameterName, getModelNames())) {
        mockRequest.setParameter(parameterName, value);
      }
    }
    return mockRequest;
  }

  private String[] getModelNames() {
    return new String[] {"page", "size", "per_page", "sort", "order", "limit"};
  }

  private boolean isPagerModelAttribute(String parameterName, String[] modelNames) {
    for (String modelName : modelNames) {
      if (parameterName.equalsIgnoreCase(modelName)) {
        return true;
      }
    }
    return false;
  }
}
