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

import static net.asany.jfantasy.framework.util.common.ObjectUtil.multipleValuesObjectsObjects;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.dao.MatchType;
import net.asany.jfantasy.framework.dao.jpa.PropertyFilter;
import net.asany.jfantasy.framework.dao.jpa.WebPropertyFilter;
import net.asany.jfantasy.framework.util.common.ObjectUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.core.MethodParameter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.reactive.BindingContext;
import org.springframework.web.server.ServerWebExchange;

@Slf4j
public class PropertyFilterModelAttributeMethodProcessor extends MethodArgumentResolver {

  protected static String[] MULTI_VALUE = new String[] {"in", "notIn"};

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return "filter".equals(parameter.getParameterName())
        && WebPropertyFilter.class.isAssignableFrom(parameter.getParameterType())
        && isIncludeEntityType(parameter);
  }

  private static boolean isIncludeEntityType(MethodParameter parameter) {
    Type type = parameter.getGenericParameterType();
    if (parameter.getParameterType() != WebPropertyFilter.class) {
      return true;
    }
    if (type instanceof ParameterizedType) {
      Type[] actualTypes = ((ParameterizedType) type).getActualTypeArguments();
      return actualTypes.length == 1;
    }
    String location =
        parameter.getMember().getDeclaringClass() + "." + parameter.getMember().getName();
    log.error("{} filter 配置错误, 泛型必填，且应该对应 JPA 的 Entity 类型, 位置 : ", location);
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
    ParameterizedType parameterType = (ParameterizedType) parameter.getGenericParameterType();
    Class<?> entityClass = (Class<?>) parameterType.getActualTypeArguments()[0];
    return PropertyFilter.newFilter(entityClass);
  }

  //  protected final Map<String, String> getUriTemplateVariables(NativeWebRequest request) {
  //    @SuppressWarnings("unchecked") Map<String, String> variables =
  //        (Map<String, String>)
  //            request.getAttribute(
  //                HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
  // RequestAttributes.SCOPE_REQUEST);
  //    return (variables != null) ? variables : Collections.<String, String>emptyMap();
  //  }

  //  protected final Map<String, String> getUriQueryVariables(NativeWebRequest request) {
  //    parseQuery(((ServletWebRequest) request).getRequest().getQueryString());
  //    return new HashMap<>();
  //  }

  //  public static Map<String, String[]> parseQuery(String query) {
  //    Map<String, String[]> params = new LinkedHashMap<>();
  //    if (StringUtil.isBlank(query)) {
  //      return params;
  //    }
  //    for (String pair : query.split("[;&]")) {
  //      String[] vs = pair.split("=");
  //      String key = vs[0];
  //      String val = vs.length == 1 ? "" : vs[1];
  //      if (StringUtil.isNotBlank(val)) {
  //        val = URLDecoder.decode(val, StandardCharsets.UTF_8);
  //      }
  //      if (!params.containsKey(key)) {
  //        params.put(key, new String[] {val});
  //      } else {
  //        params.put(key, ObjectUtil.join(params.get(key), val));
  //      }
  //    }
  //    return params;
  //  }

  @Override
  protected void bindRequestParameters(
      WebDataBinder binder, ServerHttpRequest request, MethodParameter parameter) {
    PropertyFilter target = (PropertyFilter) binder.getTarget();
    if (target == null) {
      throw new IllegalStateException("target cannot be null");
    }

    int parameterCount = Objects.requireNonNull(parameter.getMethod()).getParameterCount();

    MultiValueMap<String, String> queryParams = request.getQueryParams();

    for (String paramName : queryParams.keySet()) {

      if (parameterCount > 1) {
        if (paramName.startsWith("filter.")) {
          paramName = paramName.substring(paramName.indexOf(".") + 1);
        }
      }

      String[] slugs = StringUtil.tokenizeToStringArray(paramName, "_");

      String matchTypeStr = slugs.length > 1 ? slugs[0] : MatchType.EQ.getSlug();
      String name = slugs.length > 1 ? slugs[1] : paramName;

      if (!target.hasProperty(name)) {
        continue;
      }

      Object newValue;
      if (ObjectUtil.exists(MULTI_VALUE, matchTypeStr)) {
        newValue =
            Arrays.stream(multipleValuesObjectsObjects(queryParams.get(paramName)))
                .toArray(Object[]::new);
      } else {
        newValue = queryParams.getFirst(name);
      }

      Objects.requireNonNull(MatchType.get(matchTypeStr)).build(target, name, newValue);
    }
  }
}
