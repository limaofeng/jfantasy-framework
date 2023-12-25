package net.asany.jfantasy.framework.web.filter.wrapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.Base64Util;
import net.asany.jfantasy.framework.util.common.StringUtil;
import net.asany.jfantasy.framework.util.regexp.RegexpUtil;
import net.asany.jfantasy.framework.util.web.WebUtil;
import org.springframework.web.util.HtmlUtils;

@Slf4j
public class XSSRequestWrapper extends HttpServletRequestWrapper {

  private final Map<String, String[]> parameterMap = new LinkedHashMap<>();

  private boolean transform = false;

  public XSSRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  public XSSRequestWrapper(HttpServletRequest request, boolean transform) {
    super(request);
    this.transform = transform;
  }

  @Override
  public String getParameter(String name) {
    String[] values = getParameterValues(name);
    return values == null || values.length == 0 ? null : values[0];
  }

  @Override
  public String[] getParameterValues(String name) {
    return getParameterMap().get(name);
  }

  private String transform(String value) {
    String escapeStr;
    // 普通Html过滤
    if ("GET".equalsIgnoreCase(WebUtil.getMethod((HttpServletRequest) this.getRequest()))
        && isTransform()) {
      escapeStr =
          WebUtil.transformCoding(
              value,
              StandardCharsets.ISO_8859_1,
              Charset.forName(this.getRequest().getCharacterEncoding()));
      escapeStr = StringUtil.isNotBlank(escapeStr) ? HtmlUtils.htmlEscape(escapeStr) : escapeStr;
    } else {
      escapeStr = StringUtil.isNotBlank(value) ? HtmlUtils.htmlEscape(value) : value;
    }
    // 防止链接注入 (如果以http开头的参数,同时与请求的域不相同的话,将值64位编码)
    if (RegexpUtil.find(escapeStr, "^http://")
        && !WebUtil.getServerUrl((HttpServletRequest) super.getRequest()).startsWith(escapeStr)) {
      escapeStr = "base64:" + new String(Base64Util.encode(escapeStr.getBytes()));
    }
    return escapeStr;
  }

  private boolean isTransform() {
    return transform;
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
