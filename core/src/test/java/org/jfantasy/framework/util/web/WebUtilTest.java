package org.jfantasy.framework.util.web;

import eu.bitwalker.useragentutils.Browser;
import eu.bitwalker.useragentutils.OperatingSystem;
import eu.bitwalker.useragentutils.UserAgent;
import java.util.Arrays;
import java.util.Map;
import javax.servlet.http.Cookie;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockServletContext;
import org.springframework.util.Assert;

public class WebUtilTest {

  private static final Log LOG = LogFactory.getLog(WebUtilTest.class);

  protected MockHttpServletRequest request;
  protected MockHttpServletResponse response;

  @BeforeEach
  public void setUp() throws Exception {
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
  public void tearDown() throws Exception {}

  @Test
  public void testGetExtension() throws Exception {
    Assert.isTrue("".equals(WebUtil.getExtension("12312312.jsg")));

    Assert.isTrue("jsp".equals(WebUtil.getExtension(request)));

    Assert.isTrue("do" == WebUtil.getExtension("/test/test.do"));
  }

  @Test
  public void testGetServerName() throws Exception {
    Assert.isTrue("www.jfantasy.org" == WebUtil.getServerName(request));

    Assert.isTrue("www.jfantasy.org" == WebUtil.getServerName("http://www.jfantasy.org/test.do"));
  }

  @Test
  public void testGetScheme() throws Exception {
    Assert.isTrue("http" == WebUtil.getScheme("http://www.jfantasy.org/test.do"));

    Assert.isTrue("https" == WebUtil.getScheme("https://www.jfantasy.org/test.do"));
  }

  @Test
  public void testGetPort() throws Exception {
    Assert.isTrue(80 == WebUtil.getPort(request));

    Assert.isTrue(80 == WebUtil.getPort("http://www.jfantasy.org/test.do"));

    Assert.isTrue(443 == WebUtil.getPort("https://www.jfantasy.org/test.do"));

    Assert.isTrue(8080 == WebUtil.getPort("https://www.jfantasy.org:8080/test.do"));
  }

  @Test
  public void testAcceptEncoding() throws Exception {
    Assert.isTrue("gzip" == WebUtil.getAcceptEncoding(request));
  }

  @Test
  public void testGetReferer() throws Exception {
    Assert.isTrue("http://www.haoluesoft.com.cn" == WebUtil.getReferer(request));
  }

  @Test
  public void testGetCookie() throws Exception {
    Cookie cookie = WebUtil.getCookie(request, "username");
    Assert.isTrue("limaofeng" == cookie.getValue());
  }

  @Test
  public void testAddCookie() throws Exception {
    WebUtil.addCookie(response, "email", "limaofeng@msn.com", 100);
    Assert.isTrue("limaofeng@msn.com" == response.getCookie("email").getValue());
  }

  @Test
  public void testRemoveCookie() throws Exception {
    WebUtil.removeCookie(request, response, "username");
    Cookie cookie = response.getCookie("username");
    Assert.isTrue(0 == cookie.getMaxAge());
  }

  @Test
  public void testGetRealIpAddress() throws Exception {
    Assert.isTrue("127.0.0.1" == WebUtil.getRealIpAddress(request));
  }

  @Test
  public void testIsSelfIp() throws Exception {
    LOG.debug("isSelfIp => " + WebUtil.isSelfIp("192.168.1.200"));
  }

  @Test
  public void testGetServerIps() throws Exception {
    String[] serverIps = WebUtil.getServerIps();
    LOG.debug("getServerIps => " + Arrays.toString(serverIps));
  }

  @Test
  public void testBrowser() throws Exception {
    Browser browser = WebUtil.browser(request);
    LOG.debug(browser);
  }

  @Test
  public void testParseQuery() throws Exception {
    Map<String, String[]> params =
        WebUtil.parseQuery("username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2");

    Assert.isTrue(params.get("username")[0] == "limaofeng");
    Assert.isTrue(params.get("email")[0] == "limaofeng@msn.com");
    Assert.isTrue(params.get("arrays").length == 2);
    Assert.isTrue(params.get("arrays")[0] == "1");
    Assert.isTrue(params.get("arrays")[1] == "2");

    TUser tUser =
        WebUtil.parseQuery(
            "username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2", TUser.class);

    Assert.isTrue(tUser.getUsername() == "limaofeng");
    Assert.isTrue(tUser.getEmail() == "limaofeng@msn.com");
    Assert.isTrue(tUser.getArrays().length == 2);
    Assert.isTrue(tUser.getArrays()[0] == 1);
    Assert.isTrue(tUser.getArrays()[1] == 2);
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

  public static class TUser {
    private String username;
    private String email;
    private int[] arrays;

    public String getUsername() {
      return username;
    }

    public void setUsername(String username) {
      this.username = username;
    }

    public String getEmail() {
      return email;
    }

    public void setEmail(String email) {
      this.email = email;
    }

    public int[] getArrays() {
      return arrays;
    }

    public void setArrays(int[] arrays) {
      this.arrays = arrays;
    }
  }

  @Test
  public void testGetQueryString() throws Exception {
    String url = "username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2";
    Map<String, String[]> params = WebUtil.parseQuery(url);

    String queryUrl = WebUtil.getQueryString(params);

    LOG.debug(queryUrl);

    Assert.isTrue(url == queryUrl);
  }

  @Test
  public void testSort() throws Exception {
    String queryUrl =
        WebUtil.sort("username=limaofeng&email=limaofeng@msn.com&arrays=1&arrays=2", "username");
    LOG.debug(queryUrl);
    Assert.isTrue(queryUrl.contains("username-asc"));

    queryUrl = WebUtil.sort(queryUrl, "username");
    LOG.debug(queryUrl);
    Assert.isTrue(queryUrl.contains("username-desc"));
  }

  @Test
  public void testFilename() throws Exception {
    String filename = WebUtil.filename(new String("测试文件名称".getBytes(), "iso8859-1"), request);
    LOG.debug(filename);
  }

  @Test
  public void testTransformCoding() throws Exception {
    String filename =
        WebUtil.transformCoding(new String("测试文件名称".getBytes(), "iso8859-1"), "iso8859-1", "uft-8");
    LOG.debug(filename);
  }

  @Test
  public void testIsAjax() throws Exception {
    Assert.isTrue(!WebUtil.isAjax(request));

    request.addHeader("X-Requested-With", "XMLHttpRequest");

    Assert.isTrue(WebUtil.isAjax(request));
  }

  @Test
  public void testGetSessionId() throws Exception {
    request.setSession(new MockHttpSession(new MockServletContext(), "TESTSESSIONID"));

    LOG.debug(WebUtil.getSessionId(request));

    Assert.isTrue("TESTSESSIONID" == WebUtil.getSessionId(request));
  }

  @Test
  public void testGetMethod() throws Exception {
    Assert.isTrue("get" == WebUtil.getMethod(request));
  }

  @Test
  public void testGetRequestUrl() throws Exception {
    LOG.debug(WebUtil.getServerUrl(request));
    Assert.isTrue("http://www.jfantasy.org" == WebUtil.getServerUrl(request));
  }
}
