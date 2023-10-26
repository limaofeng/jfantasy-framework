package org.jfantasy.framework.util.web;

import static org.junit.jupiter.api.Assertions.*;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import jakarta.servlet.http.Cookie;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;

@Slf4j
public class WebUtilTest {

  protected MockHttpServletRequest request;
  protected MockHttpServletResponse response;

  @BeforeEach
  public void setUp() {
    request = new MockHttpServletRequest("get", "/test/test.jsp");
    request.setServerName("www.jfantasy.org");
    request.addHeader("Referer", "http://www.haoluesoft.com.cn");
    request.addHeader("Accept-Encoding", "gzip");

    request.addHeader("copyright", "jfantasy");
    request.addHeader(
        "User-Agent",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/101.0.4951.54 Safari/537.36");

    request.setCookies(new Cookie("username", "limaofeng"));

    response = new MockHttpServletResponse();
  }

  @AfterEach
  public void tearDown() {}

  @Test
  public void testGetExtension() {
    assertEquals("", WebUtil.getExtension("12312312.jsg"));

    assertEquals("jsp", WebUtil.getExtension(request));

    assertSame("do", WebUtil.getExtension("/test/test.do"));
  }

  @Test
  public void testGetServerName() {
    assertSame("www.jfantasy.org", WebUtil.getServerName(request));

    assertSame("www.jfantasy.org", WebUtil.getServerName("http://www.jfantasy.org/test.do"));
  }

  @Test
  public void testGetScheme() {
    assertSame("http", WebUtil.getScheme("http://www.jfantasy.org/test.do"));

    assertSame("https", WebUtil.getScheme("https://www.jfantasy.org/test.do"));
  }

  @Test
  public void testGetPort() {
    assertEquals(80, WebUtil.getPort(request));

    assertEquals(80, WebUtil.getPort("http://www.jfantasy.org/test.do"));

    assertEquals(443, WebUtil.getPort("https://www.jfantasy.org/test.do"));

    assertEquals(8080, WebUtil.getPort("https://www.jfantasy.org:8080/test.do"));
  }

  @Test
  public void testAcceptEncoding() {
    assertSame("gzip", WebUtil.getAcceptEncoding(request));
  }

  @Test
  public void testGetReferer() {
    assertSame("http://www.haoluesoft.com.cn", WebUtil.getReferer(request));
  }

  @Test
  public void testGetCookie() {
    Cookie cookie = WebUtil.getCookie(request, "username");
    assertNotNull(cookie);
    assertSame("limaofeng", cookie.getValue());
  }

  @Test
  public void testAddCookie() {
    WebUtil.addCookie(response, "email", "limaofeng@msn.com", 100);
    assertNotNull(response.getCookie("email"));
    assertSame("limaofeng@msn.com", Objects.requireNonNull(response.getCookie("email")).getValue());
  }

  @Test
  public void testRemoveCookie() {
    WebUtil.removeCookie(request, response, "username");
    Cookie cookie = response.getCookie("username");
    assertNotNull(cookie);
    assertEquals(0, cookie.getMaxAge());
  }

  @Test
  public void testGetRealIpAddress() {
    assertSame("127.0.0.1", WebUtil.getClientIP(request));
  }

  @Test
  public void testIsSelfIp() {
    log.debug("isSelfIp => " + WebUtil.isLocalIP("192.168.1.200"));
  }

  @Test
  public void testGetServerIps() {
    String[] serverIps = WebUtil.getLocalIPs();
    log.info("getServerIps => " + Arrays.toString(serverIps));
  }

  @Test
  public void testBrowser() {
    Browser browser = WebUtil.browser(request);
    log.debug(browser.toString());
  }

  @Test
  public void testParseQuery() {
    Map<String, String[]> params =
        WebUtil.parseQuery("username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2");

    assertSame("limaofeng", params.get("username")[0]);
    assertSame("limaofeng@msn.com", params.get("email")[0]);
    assertEquals(2, params.get("arrays").length);
    assertSame("1", params.get("arrays")[0]);
    assertSame("2", params.get("arrays")[1]);

    TUser tUser =
        WebUtil.parseQuery(
            "username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2", TUser.class);

    assertNotNull(tUser);

    assertSame("limaofeng", tUser.getUsername());
    assertSame("limaofeng@msn.com", tUser.getEmail());
    assertEquals(2, tUser.getArrays().length);
    assertEquals(1, tUser.getArrays()[0]);
    assertEquals(2, tUser.getArrays()[1]);
  }

  @Test
  void parseUserAgent() {
    UserAgent userAgent = WebUtil.parseUserAgent(request);
    // 获取浏览器对象
    Browser browser = userAgent.getBrowser();
    // 获取操作系统对象
    OperatingSystem operatingSystem = userAgent.getOperatingSystem();

    System.out.println("浏览器名:" + browser.getName());
    System.out.println("浏览器类型:" + browser.getBrowserType());
    System.out.println("浏览器家族:" + browser.getGroup());
    System.out.println("浏览器生产厂商:" + browser.getManufacturer());
    System.out.println("浏览器使用的渲染引擎:" + browser.getRenderingEngine());
    System.out.println("浏览器版本:" + userAgent.getBrowserVersion());

    System.out.println("操作系统名:" + operatingSystem.getName());
    System.out.println("访问设备类型:" + operatingSystem.getDeviceType());
    System.out.println("操作系统家族:" + operatingSystem.getGroup());
    System.out.println("操作系统生产厂商:" + operatingSystem.getManufacturer());
  }

  @Getter
  @Setter
  public static class TUser {
    private String username;
    private String email;
    private int[] arrays;
  }

  @Test
  public void testGetQueryString() {
    String url = "username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2";
    Map<String, String[]> params = WebUtil.parseQuery(url);

    String queryUrl = WebUtil.getQueryString(params);

    log.debug(queryUrl);

    assertSame(url, queryUrl);
  }

  @Test
  public void testSort() {
    String queryUrl =
        WebUtil.sort("username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2", "username");
    log.debug(queryUrl);
    assertTrue(queryUrl.contains("username-asc"));

    queryUrl = WebUtil.sort(queryUrl, "username");
    log.debug(queryUrl);
    assertTrue(queryUrl.contains("username-desc"));
  }

  @Test
  public void testFilename() throws UnsupportedEncodingException {
    String filename = WebUtil.filename(new String("测试文件名称".getBytes(), "iso8859-1"), request);
    log.debug(filename);
  }

  @Test
  public void testTransformCoding() throws UnsupportedEncodingException {
    String filename =
        WebUtil.transformCoding(
            new String("测试文件名称".getBytes(), "iso8859-1"),
            StandardCharsets.ISO_8859_1,
            StandardCharsets.UTF_8);
    log.debug(filename);
  }

  @Test
  public void testIsAjax() {
    assertFalse(WebUtil.isAjax(request));

    request.addHeader("X-Requested-With", "XMLHttpRequest");

    assertTrue(WebUtil.isAjax(request));
  }

  @Test
  public void testGetSessionId() {
    request.setSession(new MockHttpSession(new MockServletContext(), "TESTSESSIONID"));

    log.debug(WebUtil.getSessionId(request));

    assertSame("TESTSESSIONID", WebUtil.getSessionId(request));
  }

  @Test
  public void testGetMethod() {
    assertSame("get", WebUtil.getMethod(request));
  }

  @Test
  public void testGetRequestUrl() {
    log.debug(WebUtil.getServerUrl(request));
    assertSame("http://www.jfantasy.org", WebUtil.getServerUrl(request));
  }
}
