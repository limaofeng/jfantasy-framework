package org.jfantasy.framework.web.filter.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.web.WebUtil;

/**
 * 字符编码请求包装器
 *
 * @author limaofeng
 */
@Slf4j
public class CharacterEncodingRequestWrapper extends HttpServletRequestWrapper {

  private final Map<String, String[]> parameterMap = new LinkedHashMap<>();

  public CharacterEncodingRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getParameter(String name) {
    String[] values = getParameterValues(name);
    return ObjectUtil.defaultValue(values, new String[0]).length == 0 ? null : values[0];
  }

  @Override
  public String[] getParameterValues(String name) {
    return getParameterMap().get(name);
  }

  private String transform(String value) {
    if (StandardCharsets.ISO_8859_1.newEncoder().canEncode(value)) {
      return WebUtil.transformCoding(
          value,
          StandardCharsets.ISO_8859_1,
          Charset.forName(this.getRequest().getCharacterEncoding()));
    }
    return value;
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    if (!parameterMap.isEmpty() || super.getParameterMap().isEmpty()) {
      return parameterMap;
    }
    parameterMap.putAll(WebUtil.getParameterMap(this, this::transform));
    return parameterMap;
  }
}
