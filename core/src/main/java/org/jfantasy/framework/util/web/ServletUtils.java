package org.jfantasy.framework.util.web;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfantasy.framework.util.common.EncodeUtil;
import org.jfantasy.framework.util.common.PropertiesHelper;

/**
 * Servlet 工具类
 *
 * @author limaofeng
 */
public class ServletUtils {
  private ServletUtils() {}

  private static final Log LOGGER = LogFactory.getLog(ServletUtils.class);
  public static final String TEXT_TYPE = "text/plain";
  public static final String JSON_TYPE = "application/json";
  public static final String XML_TYPE = "text/xml";
  public static final String HTML_TYPE = "text/html";
  public static final String JS_TYPE = "text/javascript";
  public static final String EXCEL_TYPE = "application/vnd.ms-excel";
  public static final String AUTHENTICATION_HEADER = "Authorization";
  public static final long ONE_YEAR_SECONDS = 31536000L;
  private static String poweredBy = "jfantasy.app";

  static {
    try {
      poweredBy =
          PropertiesHelper.load("plugin.properties").getProperty("system.PoweredBy", "fantasy.com");
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
    }
  }

  /**
   * 设置 页面过期时间
   *
   * @param response HttpServletResponse
   * @param expiresSeconds 过期时间(秒)
   */
  public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
    response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000L);
    response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
  }

  /**
   * 设置 页面不缓存
   *
   * @param response HttpServletResponse
   */
  public static void setNoCacheHeader(HttpServletResponse response) {
    response.setDateHeader("Expires", 0);
    response.addHeader("Powered-By", poweredBy);
    response.addHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-cache");
  }

  /**
   * 设置 页面不存储
   *
   * @param response HttpServletResponse
   */
  public static void setNoStoreHeader(HttpServletResponse response) {
    response.setDateHeader("Expires", 0);
    response.addHeader("Powered-By", poweredBy);
    response.addHeader("Pragma", "no-cache");
    response.setHeader("Cache-Control", "no-store");
  }

  /**
   * 设置 页面的最后修改时间
   *
   * @param response
   * @param lastModifiedDate
   */
  public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
    response.setDateHeader("Last-Modified", lastModifiedDate);
  }

  public static void setEtag(HttpServletResponse response, String etag) {
    response.setHeader("ETag", etag);
  }

  /**
   * 检查 Modified 字段是否过期
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param lastModified 过期时间
   * @return boolean
   */
  public static boolean checkIfModifiedSince(
      HttpServletRequest request, HttpServletResponse response, long lastModified) {
    long ifModifiedSince = request.getDateHeader("If-Modified-Since");
    if ((ifModifiedSince != -1L) && (lastModified < ifModifiedSince + 1000L)) {
      response.setStatus(304);
      return false;
    }
    return true;
  }

  /**
   * 检查 etag 字段是否过期
   *
   * @param request HttpServletRequest
   * @param response HttpServletResponse
   * @param etag 版本的标识符
   * @return boolean
   */
  public static boolean checkIfNoneMatchEtag(
      HttpServletRequest request, HttpServletResponse response, String etag) {
    String headerValue = request.getHeader("If-None-Match");
    if (headerValue != null) {
      boolean conditionSatisfied = false;
      if (!"*".equals(headerValue)) {
        StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");
        do {
          String currentToken = commaTokenizer.nextToken();
          if (currentToken.trim().equals(etag)) {
            conditionSatisfied = true;
          }
          if (conditionSatisfied) {
            break;
          }
        } while (commaTokenizer.hasMoreTokens());
      } else {
        conditionSatisfied = true;
      }
      if (conditionSatisfied) {
        response.setStatus(304);
        response.setHeader("ETag", etag);
        return false;
      }
    }
    return true;
  }

  /**
   * 方法待优化
   *
   * @param response HttpServletResponse
   * @param fileName 设置下载文件名
   */
  @Deprecated
  public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
    try {
      String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
      response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
    } catch (UnsupportedEncodingException localUnsupportedEncodingException) {
      LOGGER.error(
          localUnsupportedEncodingException.getMessage(), localUnsupportedEncodingException);
    }
  }

  @SuppressWarnings("unchecked")
  public static Map<String, Object> getParametersStartingWith(
      HttpServletRequest request, String prefix) {
    Enumeration<String> paramNames = request.getParameterNames();
    Map<String, Object> params = new TreeMap<String, Object>();
    if (prefix == null) {
      prefix = "";
    }
    while ((paramNames != null) && (paramNames.hasMoreElements())) {
      String paramName = (String) paramNames.nextElement();
      if (("".equals(prefix)) || (paramName.startsWith(prefix))) {
        String unprefixed = paramName.substring(prefix.length());
        String[] values = request.getParameterValues(paramName);
        if ((values != null) && (values.length != 0)) {
          if (values.length > 1) {
            params.put(unprefixed, values);
          } else {
            params.put(unprefixed, values[0]);
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
