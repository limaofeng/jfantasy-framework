package net.asany.jfantasy.framework.util.web;

import eu.bitwalker.useragentutils.Browser;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import net.asany.jfantasy.framework.util.common.EncodeUtil;
import net.asany.jfantasy.framework.util.common.StringUtil;
import org.springframework.http.CacheControl;

/**
 * Servlet 工具类
 *
 * @author limaofeng
 */
@Slf4j
public class ServletUtils {

  public static final String TEXT_TYPE = "text/plain";
  public static final String JSON_TYPE = "application/json";
  public static final String XML_TYPE = "text/xml";
  public static final String HTML_TYPE = "text/html";
  public static final String JS_TYPE = "text/javascript";
  public static final String EXCEL_TYPE = "application/vnd.ms-excel";
  public static final String AUTHENTICATION_HEADER = "Authorization";
  public static final long ONE_YEAR_SECONDS = 31536000L;

  public static final String[] CORS_DEFAULT_ALLOWED_HEADERS =
      new String[] {
        "Range",
        "Accept",
        "Origin",
        "Cache-Control",
        "X-Requested-With",
        "Authorization",
        "Content-Type",
        "If-Modified-Since",
        "If-None-Match",
        "X-Request-ID",
        "X-Client-ID"
      };
  public static final String CORS_DEFAULT_ORIGIN_PATTERNS = "*";
  public static final String[] CORS_DEFAULT_ALLOWED_METHODS =
      new String[] {"GET", "POST", "HEAD", "PATCH", "PUT", "DELETE", "OPTIONS"};
  public static final String[] CORS_DEFAULT_EXPOSE_METHODS =
      new String[] {"ETag", "Content-Range", "Connection", "Accept-Ranges", "Content-Disposition"};
  public static final long CORS_DEFAULT_MAX_AGE = 3600;

  private ServletUtils() {}

  public static void cors(
      String allowedOriginPatterns,
      String[] allowedMethods,
      String[] allowedHeaders,
      String[] exposeHeaders,
      HttpServletResponse response) {
    response.setHeader("Access-Control-Allow-Origin", allowedOriginPatterns);
    response.setHeader("Access-Control-Allow-Methods", StringUtil.join(allowedMethods, ","));
    response.setHeader("Access-Control-Allow-Headers", StringUtil.join(allowedHeaders, ","));
    response.setHeader("Access-Control-Expose-Headers", StringUtil.join(exposeHeaders, ","));
  }

  /**
   * 缓存
   *
   * @param expires 过期时间
   * @param response 响应
   */
  public static void setCache(long expires, HttpServletResponse response) {
    setCache(expires, null, null, CacheControl.maxAge(expires, TimeUnit.SECONDS), response);
  }

  /**
   * 缓存
   *
   * @param expires 过期时间
   * @param response 响应
   */
  public static void setCache(
      long expires,
      String etag,
      Date lastModified,
      CacheControl cacheControl,
      HttpServletResponse response) {
    response.setDateHeader("Expires", System.currentTimeMillis() + expires * 1000L);
    if (StringUtil.isNotBlank(etag)) {
      response.setHeader("ETag", etag);
    }
    if (lastModified != null) {
      response.setDateHeader("Last-Modified", lastModified.getTime());
    }
    response.setHeader("Cache-Control", cacheControl.getHeaderValue());
  }

  public static boolean isNoCache(HttpServletRequest request) {
    return "no-cache".equalsIgnoreCase(request.getHeader("Cache-Control"));
  }

  /**
   * 请求时，用到了缓存
   *
   * @param request 请求
   * @return boolean
   */
  public static boolean cacheable(HttpServletRequest request) {
    String ifNoneMatch = request.getHeader("If-None-Match");
    String ifModifiedSince = request.getHeader("If-Modified-Since");
    return StringUtil.isNotBlank(ifNoneMatch) || StringUtil.isNotBlank(ifModifiedSince);
  }

  public static boolean checkCache(String etag, Date lastModified, HttpServletRequest request) {
    String ifNoneMatch = request.getHeader("If-None-Match");

    if (StringUtil.isNotBlank(ifNoneMatch)) {
      return checkIfNoneMatchEtag(etag, ifNoneMatch);
    }

    long ifModifiedSince = request.getDateHeader("If-Modified-Since");

    if (StringUtil.isNotBlank(ifModifiedSince)) {
      return checkIfModifiedSince(lastModified, ifModifiedSince);
    }

    return false;
  }

  public static void setNotModified(HttpServletResponse response) {
    response.setStatus(304);
  }

  /**
   * 设置 页面不缓存
   *
   * @param response HttpServletResponse
   */
  public static void setNoCache(HttpServletResponse response) {
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", CacheControl.noCache().getHeaderValue());
  }

  /**
   * 检查 Modified 字段是否过期
   *
   * @param lastModified 过期时间
   * @param ifModifiedSince HttpServletRequest
   * @return boolean 命中返回 true
   */
  public static boolean checkIfModifiedSince(Date lastModified, long ifModifiedSince) {
    return (ifModifiedSince != -1L) && (lastModified.getTime() < ifModifiedSince + 1000L);
  }

  /**
   * 检查 etag 字段是否过期
   *
   * @param etag 版本的标识符
   * @param ifNoneMatch HttpServletRequest
   * @return boolean 命中返回 true
   */
  public static boolean checkIfNoneMatchEtag(String etag, String ifNoneMatch) {
    if ("*".equals(ifNoneMatch)) {
      return true;
    }
    boolean conditionSatisfied = false;
    StringTokenizer commaTokenizer = new StringTokenizer(ifNoneMatch, ",");
    do {
      String currentToken = commaTokenizer.nextToken();
      if (currentToken.trim().equals(etag)) {
        conditionSatisfied = true;
      }
      if (conditionSatisfied) {
        break;
      }
    } while (commaTokenizer.hasMoreTokens());
    return conditionSatisfied;
  }

  /**
   * 设置内容该以何种形式展示
   *
   * @param type 类型 inline | attachment
   * @param fileName 设置下载文件名
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   */
  public static void setContentDisposition(
      String type, String fileName, HttpServletRequest request, HttpServletResponse response) {
    fileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);
    if (Browser.MOZILLA == WebUtil.browser(request)) {
      byte[] bytes = fileName.getBytes(StandardCharsets.UTF_8);
      fileName = new String(bytes, StandardCharsets.ISO_8859_1);
    }
    response.setHeader("Content-Disposition", type + "; filename=\"" + fileName + "\"");
  }

  public static boolean isRange(HttpServletRequest request) {
    return StringUtil.isNotBlank(request.getHeader("Range"));
  }

  public static String getRange(HttpServletRequest request) {
    return request.getHeader("Range");
  }

  public static long[] getRange(HttpServletRequest request, long length) {
    return getRange(request.getHeader("Range"), length);
  }

  public static long[] getRange(String range, long length) {
    range = StringUtil.defaultValue(range, "bytes=0-");
    String bytes = WebUtil.parseQuery(range).get("bytes")[0];
    String[] sf = bytes.split("-");
    long start = 0;
    long end = 0;
    if (sf.length == 2) {
      start = Long.parseLong(sf[0]);
      end = Long.parseLong(sf[1]);
    } else if (bytes.startsWith("-")) {
      end = length - 1;
    } else if (bytes.endsWith("-")) {
      start = Long.parseLong(sf[0]);
      end = length - 1;
    }
    return new long[] {start, end};
  }

  public static void setContentRange(
      long start, long end, long length, HttpServletResponse response) {
    long contentLength = end - start + 1;
    long rangeEnd = (end != 1 && end >= length ? end - 1 : end);
    response.setHeader("Accept-Ranges", "bytes");
    response.setHeader("Connection", "close");
    response.setHeader("Content-Range", "bytes " + start + "-" + rangeEnd + "/" + length);
  }

  public static Map<String, Object> getParametersStartingWith(
      String prefix, HttpServletRequest request) {
    Enumeration<String> paramNames = request.getParameterNames();
    Map<String, Object> params = new TreeMap<>();
    if (prefix == null) {
      prefix = "";
    }
    while ((paramNames != null) && (paramNames.hasMoreElements())) {
      String paramName = paramNames.nextElement();
      if (("".equals(prefix)) || (paramName.startsWith(prefix))) {
        String unPrefixed = paramName.substring(prefix.length());
        String[] values = request.getParameterValues(paramName);
        if ((values != null) && (values.length != 0)) {
          if (values.length > 1) {
            params.put(unPrefixed, values);
          } else {
            params.put(unPrefixed, values[0]);
          }
        }
      }
    }
    return params;
  }

  public static String encodeHttpBasic(String userName, String password) {
    String encode = userName + ":" + password;
    return "Basic " + EncodeUtil.base64Encode(encode.getBytes());
  }
}
