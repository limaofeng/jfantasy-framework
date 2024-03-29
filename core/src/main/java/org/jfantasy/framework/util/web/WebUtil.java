package org.jfantasy.framework.util.web;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.UserAgent;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.jfantasy.framework.util.common.ClassUtil;
import org.jfantasy.framework.util.common.ObjectUtil;
import org.jfantasy.framework.util.common.StringUtil;
import org.jfantasy.framework.util.ognl.OgnlUtil;
import org.jfantasy.framework.util.regexp.RegexpUtil;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * web 工具类<br>
 * web开发中经常使用的方法
 *
 * @author 李茂峰
 * @version 1.0
 * @since 2013-9-10 上午9:16:01
 */
@Slf4j
public class WebUtil {

  public static final List<String> LOCAL_IP_ADDRESS = Arrays.asList("0:0:0:0:0:0:0:1", "127.0.0.1");

  private WebUtil() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * 获取请求URL的后缀名
   *
   * @param request HttpServletRequest
   * @return {String}
   */
  public static String getExtension(HttpServletRequest request) {
    return getExtension(request.getRequestURI());
  }

  public static String getExtension(String requestUri) {
    return getExtension(requestUri, false);
  }

  /**
   * 获取请求URL的后缀名
   *
   * @param requestUri 请求路径
   * @param pure 为 True 时, 不包含开头的 "."
   * @return {String}
   */
  public static String getExtension(String requestUri, boolean pure) {
    int lastIndex = requestUri.lastIndexOf(".");
    if (lastIndex != -1) {
      return requestUri.substring(lastIndex + (pure ? 1 : 0));
    }
    return "";
  }

  public static String getServerUrl(HttpServletRequest request) {
    return getFullUrl(
        request.getScheme(),
        request.getServerName(),
        request.getServerPort(),
        request.getContextPath());
  }

  public static String getFullUrl(HttpServletRequest request, String path) {
    return getFullUrl(request.getScheme(), request.getServerName(), request.getServerPort(), path);
  }

  private static final Map<String, Integer> DEFAULT_SCHEME_PORTS =
      new HashMap() {
        {
          this.put("http", 80);
          this.put("https", 443);
        }
      };

  public static String getFullUrl(String scheme, String serverName, int serverPort, String path) {
    scheme = scheme.toLowerCase();
    StringBuilder url = new StringBuilder();
    url.append(scheme).append("://").append(serverName);
    if (!(DEFAULT_SCHEME_PORTS.containsKey(scheme)
        && DEFAULT_SCHEME_PORTS.get(scheme).equals(serverPort))) {
      url.append(":").append(serverPort);
    }
    url.append(path);
    return url.toString();
  }

  /**
   * 作用不大 不推荐使用
   *
   * @param request HttpServletRequest
   * @return {String}
   */
  public static String getServerName(HttpServletRequest request) {
    return getServerName(request.getRequestURL().toString());
  }

  /**
   * 作用不大 不推荐使用
   *
   * @param url 路径
   * @return {String}
   */
  public static String getServerName(String url) {
    url = RegexpUtil.replaceFirst(url, "^" + getScheme(url) + "://", "");
    String serverName = RegexpUtil.parseFirst(url, "[^/]+");
    return RegexpUtil.replaceFirst(serverName, ":" + getPort(serverName), "");
  }

  /**
   * 获取协议名称
   *
   * @param url 路径
   * @return {String}
   */
  public static String getScheme(String url) {
    return RegexpUtil.parseFirst(url, "^(https|http)");
  }

  /**
   * 获取请求的端口号
   *
   * @param url 路径
   * @return {String}
   */
  public static int getPort(String url) {
    String scheme = getScheme(url);
    String port = RegexpUtil.parseGroup(url, ":([0-9]+)", 1);
    return Integer.parseInt(port == null ? "http".equals(scheme) ? "80" : "443" : port);
  }

  /**
   * 获取请求的端口号
   *
   * @param request 路径
   * @return {String}
   */
  public static int getPort(HttpServletRequest request) {
    return request.getLocalPort();
  }

  /**
   * HTTP Header中Accept-Encoding 是浏览器发给服务器,声明浏览器支持的编码类型.常见的有 Accept-Encoding: compress, gzip
   * //支持compress 和gzip类型 Accept-Encoding: //默认是identity Accept-Encoding: * //支持所有类型
   * Accept-Encoding: compress;q=0.5, gzip;q=1.0//按顺序支持 gzip , compress Accept-Encoding: gzip;q=1.0,
   * identity; q=0.5, *;q=0 // 按顺序支持 gzip , identity
   *
   * @param request HTTP 请求对象
   * @return Accept-Encoding
   */
  public static String getAcceptEncoding(HttpServletRequest request) {
    return request.getHeader("Accept-Encoding");
  }

  public static String getReferer(HttpServletRequest request) {
    return request.getHeader("Referer");
  }

  private static Cookie[] getCookies(HttpServletRequest request) {
    return request.getCookies();
  }

  public static Cookie getCookie(HttpServletRequest request, String name) {
    Cookie[] cookies = getCookies(request);
    if (cookies != null) {
      for (Cookie cook : cookies) {
        if (cook.getName().equals(name)) {
          return cook;
        }
      }
    }
    return null;
  }

  public static void addCookie(
      HttpServletResponse response, String name, String value, int expiry) {
    Cookie cookie = new Cookie(name, value);
    cookie.setSecure(true);
    cookie.setMaxAge(expiry);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  public static void removeCookie(
      HttpServletRequest request, HttpServletResponse response, String name) {
    Cookie cookie = getCookie(request, name);
    if (cookie != null) {
      cookie.setMaxAge(0);
      cookie.setPath("/");
      response.addCookie(cookie);
    }
  }

  public static String getRealIpAddress(HttpServletRequest request) {
    String ip = request.getHeader("x-forwarded-for");
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getHeader("Proxy-Client-IP");
    }
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getHeader("WL-Proxy-Client-IP");
    }
    if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
      ip = request.getRemoteAddr();
    }
    if (ip == null) {
      return null;
    }
    if (ip.contains(",")) {
      return ip.split(",")[0];
    }
    if (LOCAL_IP_ADDRESS.contains(ip)) {
      return null;
    }
    return ip;
  }

  public static boolean isSelfIp(String ip) {
    try {
      if (InetAddress.getLocalHost().getHostAddress().equals(ip.trim())) {
        return true;
      }
      Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
      while (netInterfaces.hasMoreElements()) {
        NetworkInterface ni = netInterfaces.nextElement();
        Enumeration<InetAddress> ips = ni.getInetAddresses();
        while (ips.hasMoreElements()) {
          InetAddress ia = ips.nextElement();
          // 只获取IPV4的局域网和广域网地址，忽略本地回环和本地链路地址
          if (ia instanceof Inet4Address
              && ia.getHostAddress().equals(ip.trim())
              && (ia.isSiteLocalAddress() || ia.isMCGlobal())) {
            return true;
          }
        }
      }
    } catch (SocketException | UnknownHostException e) {
      log.error(e.getMessage(), e);
    }
    return false;
  }

  /**
   * 获取服务器的本地 ip
   *
   * @return {String[]}
   */
  public static String[] getServerIps() {
    String[] serverIps = new String[0];
    Enumeration<NetworkInterface> netInterfaces;
    try {
      netInterfaces = NetworkInterface.getNetworkInterfaces();
      while (netInterfaces.hasMoreElements()) {
        NetworkInterface ni = netInterfaces.nextElement();
        Enumeration<InetAddress> ips = ni.getInetAddresses();
        while (ips.hasMoreElements()) {
          InetAddress ia = ips.nextElement();
          // 只获取IPV4的局域网和广域网地址，忽略本地回环和本地链路地址
          if (ia instanceof Inet4Address && (ia.isSiteLocalAddress() || ia.isMCGlobal())) {
            serverIps = ObjectUtil.join(serverIps, ia.getHostAddress());
          }
        }
      }
    } catch (SocketException e) {
      log.error(e.getMessage(), e);
    }
    return serverIps;
  }

  public static Browser browser(HttpServletRequest request) {
    return parseUserAgent(request).getBrowser();
  }
  /**
   * 将请求参数转换为Map
   *
   * @param query 请求参数字符串
   * @return {Map<String,String[]>}
   */
  public static Map<String, String[]> parseQuery(String query) {
    return parseQuery(query, false);
  }

  public static <T> Map<String, T> parseQuery(String query, boolean single) {
    Map<String, T> params = new LinkedHashMap<>();
    if (StringUtil.isBlank(query)) {
      return params;
    }
    for (String pair : query.split("[;&]")) {
      String[] vs = pair.split("=");
      String key = vs[0];
      if (single && params.containsKey(key)) {
        continue;
      }
      String val = vs.length == 1 ? "" : vs[1];
      if (StringUtil.isNotBlank(val)) {
        String newVal = val;
        if (StandardCharsets.US_ASCII.newEncoder().canEncode(val)) {
          newVal = StringUtil.decodeURI(val, "utf-8");
          log.debug(key + " 的原始编码为[ASCII]转编码:" + val + "=>" + newVal);
        } else if (StandardCharsets.ISO_8859_1.newEncoder().canEncode(val)) {
          newVal = WebUtil.transformCoding(val, "ISO-8859-1", "utf-8");
          log.debug(key + " 的原始编码为[ISO-8859-1]转编码:" + val + "=>" + newVal);
        }
        val = newVal;
      }
      if (!params.containsKey(key)) {
        params.put(key, (T) (single ? val : new String[] {val}));
      } else {
        params.put(key, (T) ObjectUtil.join((String[]) params.get(key), val));
      }
    }
    return params;
  }

  /**
   * 将请求参数转为指定的对象
   *
   * @param query 请求参数字符串
   * @param classes 要转换成的FormBean
   * @return T 对象
   */
  public static <T> T parseQuery(String query, Class<T> classes) {
    T t = ClassUtil.newInstance(classes);
    if (ObjectUtil.isNull(t)) {
      return null;
    }
    for (Map.Entry<String, String[]> entry : parseQuery(query).entrySet()) {
      if (entry.getValue().length == 1) {
        OgnlUtil.getInstance().setValue(entry.getKey(), t, (entry.getValue())[0]);
      } else {
        OgnlUtil.getInstance().setValue(entry.getKey(), t, entry.getValue());
      }
    }
    return t;
  }

  public static String getQueryString(Map<String, ?> params) {
    StringBuilder queryString = new StringBuilder();
    for (Map.Entry<String, ?> entry : params.entrySet()) {
      Object value = entry.getValue();
      if (value.getClass().isArray()) {
        for (int i = 0, length = Array.getLength(value); i < length; i++) {
          queryString.append(entry.getKey()).append("=").append(Array.get(value, i)).append("&");
        }
      } else {
        queryString.append(entry.getKey()).append("=").append(value).append("&");
      }
    }
    return queryString.toString().replaceAll("&$", "");
  }

  public static String sort(String queryString, String orderBy) {
    Map<String, String[]> params = parseQuery(queryString);
    if (params.containsKey("sort")) {
      String value = (params.get("sort"))[0];
      if (value.startsWith(orderBy)) {
        return queryString.replace(
            "sort=" + value,
            "sort=" + orderBy + ("asc".equals(value.split("-")[1]) ? "-desc" : "-asc"));
      }
      return queryString.replace("sort=" + value, "sort=" + orderBy + "-asc");
    }

    return queryString.replace("&$", "").concat("&sort=" + orderBy + "-asc");
  }

  public static Map<String, String> getParameterMap(HttpServletRequest request) {
    Map<String, String> parameter = new LinkedHashMap<>();
    Set<Map.Entry<String, String[]>> entries = request.getParameterMap().entrySet();
    for (Map.Entry<String, String[]> entry : entries) {
      parameter.put(entry.getKey(), entry.getValue()[0]);
    }
    return parameter;
  }

  public static boolean has(HttpServletRequest request, RequestMethod method) {
    return hasMethod(request, method.name());
  }

  public static boolean hasMethod(HttpServletRequest request, String method) {
    return method.equalsIgnoreCase(request.getMethod());
  }

  public static UserAgent parseUserAgent(HttpServletRequest request) {
    return UserAgent.parseUserAgentString(request.getHeader("User-Agent"));
  }

  public static String filename(String name, HttpServletRequest request) {
    try {
      UserAgent userAgent = parseUserAgent(request);
      return Browser.MOZILLA == userAgent.getBrowser()
          ? new String(name.getBytes(StandardCharsets.UTF_8), "iso8859-1")
          : URLEncoder.encode(name, "UTF-8");
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      return name;
    }
  }

  public static String transformCoding(String str, String oldCharset, String charset) {
    try {
      return new String(str.getBytes(oldCharset), charset);
    } catch (UnsupportedEncodingException e) {
      log.error(e.getMessage(), e);
      return str;
    }
  }

  public static boolean isAjax(HttpServletRequest request) {
    return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
  }

  public static String getSessionId(HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    return session != null ? session.getId() : null;
  }

  public static String getMethod(HttpServletRequest request) {
    return request.getMethod();
  }
}
