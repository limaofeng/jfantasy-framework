package org.jfantasy.framework.spring.mvc.method.annotation;

import org.jfantasy.framework.dao.Pager;
import org.jfantasy.framework.spring.mvc.error.RestException;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartHttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartRequest;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class PagerModelAttributeMethodProcessor implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Pager.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public final Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest request, WebDataBinderFactory binderFactory) throws Exception {
        String name = getParameterName(parameter);

        Object target = mavContainer.containsAttribute(name) ? mavContainer.getModel().get(name) : createAttribute(name, parameter, binderFactory, request);
        WebDataBinder binder = binderFactory.createBinder(request, target, name);
        target = binder.getTarget();
        if (target != null) {
            bindRequestParameters(mavContainer, binderFactory, binder, request, parameter);

            validateIfApplicable(binder, parameter);
            if (binder.getBindingResult().hasErrors() && isBindExceptionRequired(binder, parameter)) {
                throw new BindException(binder.getBindingResult());
            }
        }

        target = binder.convertIfNecessary(binder.getTarget(), parameter.getParameterType());
        if (StringUtil.isNotBlank(name)) {
            mavContainer.addAttribute(name, target);
        }
        return target;
    }

    protected String getParameterName(MethodParameter parameter) {
        return parameter.getParameterName();
    }

    protected Object createAttribute(String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        String value = getRequestValueForAttribute(attributeName, request);
        if (value != null) {
            Object attribute = createAttributeFromRequestValue(value, attributeName, parameter, binderFactory, request);
            if (attribute != null) {
                return attribute;
            }
        }
        Class<?> parameterType = parameter.getParameterType();
        if (parameterType.isArray() || List.class.isAssignableFrom(parameterType)) {
            return new Pager();
        }
        return BeanUtils.instantiateClass(parameter.getParameterType());
    }

    protected void bindRequestParameters(ModelAndViewContainer mavContainer, WebDataBinderFactory binderFactory, WebDataBinder binder, NativeWebRequest request, MethodParameter parameter) throws Exception {
        ServletRequest servletRequest = prepareServletRequest(binder.getTarget(), request, parameter);
        Pager target = (Pager) binder.getTarget();
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
                    limits = new Integer[]{0, limits[0]};
                }
                target.setFirst(limits[0]);
                target.setPageSize(limits[1]);
            } else if ("page".equalsIgnoreCase(paramName)) {
                target.setCurrentPage(Integer.valueOf(value));
            } else if ("per_page".equalsIgnoreCase(paramName)) {
                target.setPageSize(Integer.valueOf(value));
            } else if ("sort".equalsIgnoreCase(paramName)) {
                target.setOrderBy(value);
            } else if ("order".equalsIgnoreCase(paramName)) {
                target.setOrder(value);
            }
        }
    }

    protected ServletRequest prepareServletRequest(Object target, NativeWebRequest request, MethodParameter parameter) {
        HttpServletRequest nativeRequest = (HttpServletRequest) request.getNativeRequest();
        MultipartRequest multipartRequest = WebUtils.getNativeRequest(nativeRequest, MultipartRequest.class);
        MockHttpServletRequest mockRequest;
        if (multipartRequest != null) {
            MockMultipartHttpServletRequest mockMultipartRequest = new MockMultipartHttpServletRequest();
            mockMultipartRequest.getMultiFileMap().putAll(multipartRequest.getMultiFileMap());
            mockRequest = mockMultipartRequest;
        } else {
            mockRequest = new MockHttpServletRequest();
        }
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
        return new String[]{"page", "size", "per_page", "sort", "order","limit"};
    }

    private boolean isPagerModelAttribute(String parameterName, String[] modelNames) {
        for (String modelName : modelNames) {
            if (parameterName.equalsIgnoreCase(modelName)) {
                return true;
            }
        }
        return false;
    }

    String getRequestValueForAttribute(String attributeName, NativeWebRequest request) {
        Map<String, String> variables = getUriTemplateVariables(request);
        if (StringUtils.hasText(variables.get(attributeName))) {
            return variables.get(attributeName);
        } else if (StringUtils.hasText(request.getParameter(attributeName))) {
            return request.getParameter(attributeName);
        } else {
            return null;
        }
    }

    protected Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
        Map<String, String> variables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        return (variables != null) ? variables : Collections.<String, String>emptyMap();
    }

    protected void validateIfApplicable(WebDataBinder binder, MethodParameter parameter) {
        Annotation[] annotations = parameter.getParameterAnnotations();
        for (Annotation annot : annotations) {
            if (annot.annotationType().getSimpleName().startsWith("Valid")) {
                Object hints = AnnotationUtils.getValue(annot);
                binder.validate(hints instanceof Object[] ? (Object[]) hints : new Object[]{hints});
            }
        }
    }

    Object createAttributeFromRequestValue(String sourceValue, String attributeName, MethodParameter parameter, WebDataBinderFactory binderFactory, NativeWebRequest request) throws Exception {
        DataBinder binder = binderFactory.createBinder(request, null, attributeName);
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

    protected boolean isBindExceptionRequired(WebDataBinder binder, MethodParameter parameter) {
        int i = parameter.getParameterIndex();
        Class<?>[] paramTypes = parameter.getMethod().getParameterTypes();
        boolean hasBindingResult = paramTypes.length > (i + 1) && Errors.class.isAssignableFrom(paramTypes[i + 1]);

        return !hasBindingResult;
    }

}
